package mkoner.ads_dental_surgeries.service.impl;

import mkoner.ads_dental_surgeries.dto.user.UserRequestDTO;
import mkoner.ads_dental_surgeries.dto.user.UserResponseDTO;
import mkoner.ads_dental_surgeries.dto.user.UserUpdateDTO;
import mkoner.ads_dental_surgeries.exception.custom_exception.BadRequestException;
import mkoner.ads_dental_surgeries.exception.custom_exception.ResourceNotFoundException;
import mkoner.ads_dental_surgeries.mapper.UserMapper;
import mkoner.ads_dental_surgeries.model.Role;
import mkoner.ads_dental_surgeries.model.User;
import mkoner.ads_dental_surgeries.repository.RoleRepository;
import mkoner.ads_dental_surgeries.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testFindUserById_ShouldReturnUserResponseDTO() {
        User user = new User(); user.setUserId(1L);
        UserResponseDTO dto = new UserResponseDTO(1L, "firstName", "lastName", "1234567890", "email@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.mapToUserResponseDTO(user)).thenReturn(dto);

        var result = userService.findUserById(1L);
        assertEquals(dto, result);
    }

    @Test
    void testFindUserById_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.findUserById(1L));
    }

    @Test
    void testGetAllUsers_ShouldReturnList() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.mapToUserResponseDTO(any(User.class))).thenReturn(new UserResponseDTO(1L, "firstName", "lastName", "1234567890", "email@email.com"));

        var result = userService.getAllUsers();
        assertEquals(2, result.size());
    }

    @Test
    void testCreateUser_ShouldCreateAndReturnUser() {
        UserRequestDTO request = new UserRequestDTO("John", "Doe", "1234567890", "john@example.com", "password", "USER");
        Role role = new Role("USER");
        User user = new User("John", "Doe", "1234567890", "john@example.com", "password", role);
        user.setUserId(1L);
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "John", "Doe", "1234567890", "john@example.com");

        when(userRepository.findByEmailAddress("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.empty());
        when(userMapper.mapToUser(request)).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.mapToUserResponseDTO(user)).thenReturn(responseDTO);

        var result = userService.createUser(request);
        assertEquals(responseDTO, result);
    }

    @Test
    void testDeleteUser_ShouldDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);
        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_ShouldThrowBadRequest_WhenDataIntegrityViolation() {
        doThrow(DataIntegrityViolationException.class).when(userRepository).deleteById(1L);
        assertThrows(BadRequestException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void testUpdateUser_ShouldUpdateFieldsAndReturnUserResponseDTO() {
        Long id = 1L;
        User existing = new User(); existing.setUserId(id);
        Role newRole = new Role("ADMIN");
        UserUpdateDTO updateDTO = new UserUpdateDTO("Updated", "User", "updated@example.com", "0987654321", "ADMIN");
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "Updated", "User", "updated@example.com", "0987654321");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmailAddress(updateDTO.email())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(updateDTO.phoneNumber())).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName("ADMIN")).thenReturn(Optional.of(newRole));
        when(userRepository.save(any(User.class))).thenReturn(existing);
        when(userMapper.mapToUserResponseDTO(existing)).thenReturn(responseDTO);

        var result = userService.updateUser(id, updateDTO);
        assertEquals(responseDTO, result);
    }
}
