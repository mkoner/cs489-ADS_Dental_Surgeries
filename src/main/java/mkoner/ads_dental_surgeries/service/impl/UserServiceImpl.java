package mkoner.ads_dental_surgeries.service.impl;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.user.UserRequestDTO;
import mkoner.ads_dental_surgeries.dto.user.UserResponseDTO;
import mkoner.ads_dental_surgeries.dto.user.UserUpdateDTO;
import mkoner.ads_dental_surgeries.exception.BadRequestException;
import mkoner.ads_dental_surgeries.exception.ResourceNotFoundException;
import mkoner.ads_dental_surgeries.mapper.UserMapper;
import mkoner.ads_dental_surgeries.model.Role;
import mkoner.ads_dental_surgeries.model.User;
import mkoner.ads_dental_surgeries.repository.RoleRepository;
import mkoner.ads_dental_surgeries.repository.UserRepository;
import mkoner.ads_dental_surgeries.service.UserService;
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


    public UserResponseDTO findUserById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return userMapper.mapToUserResponseDTO(user);
    }

    public UserResponseDTO findByEmail(String email) {
        var user = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
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
        String roleName = user.getRole().getRoleName();
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        user.setRole(role);
        return userMapper.mapToUserResponseDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public Role createOrUpdateRole(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
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
}

