package mkoner.ads_dental_surgeries.service.impl;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.user.UserFilterDTO;
import mkoner.ads_dental_surgeries.dto.user.UserRequestDTO;
import mkoner.ads_dental_surgeries.dto.user.UserResponseDTO;
import mkoner.ads_dental_surgeries.dto.user.UserUpdateDTO;
import mkoner.ads_dental_surgeries.exception.custom_exception.BadRequestException;
import mkoner.ads_dental_surgeries.exception.custom_exception.ResourceNotFoundException;
import mkoner.ads_dental_surgeries.filter_specification.UserSpecification;
import mkoner.ads_dental_surgeries.mapper.UserMapper;
import mkoner.ads_dental_surgeries.model.Role;
import mkoner.ads_dental_surgeries.model.User;
import mkoner.ads_dental_surgeries.repository.RoleRepository;
import mkoner.ads_dental_surgeries.repository.UserRepository;
import mkoner.ads_dental_surgeries.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    public UserResponseDTO findUserById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return userMapper.mapToUserResponseDTO(user);
    }


    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll().stream()
                .map(userMapper::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        // check if email is already in use
        userRepository.findByEmailAddress(userRequestDTO.email())
                .ifPresent(u -> { throw new BadRequestException("Email is already in use by another user"); });

        // Check if phone number is taken by another user
        userRepository.findByPhoneNumber(userRequestDTO.phoneNumber())
                .ifPresent(u -> { throw new BadRequestException("Phone number is already in use by another user"); });

        var user = userMapper.mapToUser(userRequestDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String roleName = user.getRole().getRoleName();
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        user.setRole(role);
        return userMapper.mapToUserResponseDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        try{
            userRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            System.out.println(e);
            throw new BadRequestException("Deletion failed due to associated records");
        }
        catch (Exception e){
            System.out.println(e);
            throw new BadRequestException("Deletion failed");
        }
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        var existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        // Check if email is taken by another user
        userRepository.findByEmailAddress(userUpdateDTO.email())
                .filter(user -> !user.getUserId().equals(id))
                .ifPresent(u -> { throw new BadRequestException("Email is already in use by another user"); });

        // Check if phone number is taken by another user
        userRepository.findByPhoneNumber(userUpdateDTO.phoneNumber())
                .filter(user -> !user.getUserId().equals(id))
                .ifPresent(u -> { throw new BadRequestException("Phone number is already in use by another user"); });

        String roleName = userUpdateDTO.role();
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
        existingUser.setRole(role);
        existingUser.setFirstName(userUpdateDTO.firstName());
        existingUser.setLastName(userUpdateDTO.lastName());
        existingUser.setPhoneNumber(userUpdateDTO.phoneNumber());
        existingUser.setEmailAddress(userUpdateDTO.email());
        return userMapper.mapToUserResponseDTO(userRepository.save(existingUser));
    }

    public Page<UserResponseDTO> getFilteredUsersWithPagination(UserFilterDTO filterDTO, Pageable pageable) {
        Specification<User> spec = Specification.where(null);

        if (filterDTO.firstName() != null) {
            spec = spec.and(UserSpecification.hasFirstName(filterDTO.firstName()));
        }
        if (filterDTO.lastName() != null) {
            spec = spec.and(UserSpecification.hasLastName(filterDTO.lastName()));
        }
        if (filterDTO.phoneNumber() != null) {
            spec = spec.and(UserSpecification.hasPhoneNumber(filterDTO.phoneNumber()));
        }
        if (filterDTO.emailAddress() != null) {
            spec = spec.and(UserSpecification.hasEmailAddress(filterDTO.emailAddress()));
        }
        if (filterDTO.roleName() != null) {
            spec = spec.and(UserSpecification.hasRoleName(filterDTO.roleName()));
        }

        Page<User> users = userRepository.findAll(spec, pageable);
        return users.map(userMapper::mapToUserResponseDTO);
    }

}

