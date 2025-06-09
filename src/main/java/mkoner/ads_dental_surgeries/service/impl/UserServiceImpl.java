package mkoner.ads_dental_surgeries.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    public UserResponseDTO findUserById(Long id) {
        log.debug("Finding user by id {}", id);
        var user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User with id {} not found", id);
                    return new ResourceNotFoundException("User with id " + id + " not found");
                });
        log.info("Retrieved user with id {}", id);
        return userMapper.mapToUserResponseDTO(user);
    }


    public List<UserResponseDTO> getAllUsers() {
        log.debug("Finding all users");
        var users = userRepository.findAll().stream()
                .map(userMapper::mapToUserResponseDTO)
                .toList();
        log.info("Retrieved all {} users", users.size());
        return users;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        log.info("Creating new user with email: {}", userRequestDTO.email());

        // Check if email is already in use
        userRepository.findByEmailAddress(userRequestDTO.email())
                .ifPresent(u -> {
                    log.warn("Email {} is already in use", userRequestDTO.email());
                    throw new BadRequestException("Email is already in use by another user");
                });

        // Check if phone number is already in use
        userRepository.findByPhoneNumber(userRequestDTO.phoneNumber())
                .ifPresent(u -> {
                    log.warn("Phone number {} is already in use", userRequestDTO.phoneNumber());
                    throw new BadRequestException("Phone number is already in use by another user");
                });

        var user = userMapper.mapToUser(userRequestDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        String roleName = user.getRole().getRoleName();
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    log.info("Role '{}' not found, creating new role entry", roleName);
                    return roleRepository.save(new Role(roleName));
                });

        user.setRole(role);
        User savedUser = userRepository.save(user);

        log.info("Successfully created user with ID: {}", savedUser.getUserId());
        return userMapper.mapToUserResponseDTO(savedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        try{
            userRepository.deleteById(id);
            log.info("Successfully deleted user with ID {}", id);
        }
        catch (DataIntegrityViolationException e) {
            log.warn("Deletion user for patient ID {} due to data integrity violation: {}", id, e.getMessage());
            throw new BadRequestException("Deletion failed due to associated records");
        }
        catch (Exception e){
            log.error("Unexpected error occurred while deleting patient ID {}: {}", id, e.getMessage(), e);
            throw new BadRequestException("Deletion failed");
        }
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        log.info("Attempting to update user with ID: {}", id);

        var existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with ID {} not found", id);
                    return new ResourceNotFoundException("User with id " + id + " not found");
                });

        // Check if email is taken by another user
        userRepository.findByEmailAddress(userUpdateDTO.email())
                .filter(user -> !user.getUserId().equals(id))
                .ifPresent(u -> {
                    log.warn("Email {} is already in use by another user", userUpdateDTO.email());
                    throw new BadRequestException("Email is already in use by another user");
                });

        // Check if phone number is taken by another user
        userRepository.findByPhoneNumber(userUpdateDTO.phoneNumber())
                .filter(user -> !user.getUserId().equals(id))
                .ifPresent(u -> {
                    log.warn("Phone number {} is already in use by another user", userUpdateDTO.phoneNumber());
                    throw new BadRequestException("Phone number is already in use by another user");
                });

        String roleName = userUpdateDTO.role();
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    log.info("Role '{}' not found. Creating new role.", roleName);
                    return roleRepository.save(new Role(roleName));
                });

        existingUser.setRole(role);
        existingUser.setFirstName(userUpdateDTO.firstName());
        existingUser.setLastName(userUpdateDTO.lastName());
        existingUser.setPhoneNumber(userUpdateDTO.phoneNumber());
        existingUser.setEmailAddress(userUpdateDTO.email());

        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated user with ID: {}", updatedUser.getUserId());

        return userMapper.mapToUserResponseDTO(updatedUser);
    }

    public Page<UserResponseDTO> getFilteredUsersWithPagination(UserFilterDTO filterDTO, Pageable pageable) {
        log.debug("Getting filtered users with pagination: {}", filterDTO);
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
        log.info("Successfully getting {} users on  page: {}", users.getTotalElements(), pageable.getPageNumber());
        return users.map(userMapper::mapToUserResponseDTO);
    }

}

