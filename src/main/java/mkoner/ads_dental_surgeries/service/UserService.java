package mkoner.ads_dental_surgeries.service;

import mkoner.ads_dental_surgeries.dto.user.UserFilterDTO;
import mkoner.ads_dental_surgeries.dto.user.UserRequestDTO;
import mkoner.ads_dental_surgeries.dto.user.UserResponseDTO;
import mkoner.ads_dental_surgeries.dto.user.UserUpdateDTO;
import mkoner.ads_dental_surgeries.model.Role;
import mkoner.ads_dental_surgeries.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponseDTO findUserById(Long id);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO findByEmail(String email);
    UserResponseDTO createUser(UserRequestDTO UserRequestDTO);
    void deleteUser(Long id);
    Role createOrUpdateRole(String roleName);
    List<Role> getAllRoles();
    UserResponseDTO updateUser(Long id, UserUpdateDTO userUpdateDTO);
    Page<UserResponseDTO> getFilteredUsersWithPagination(UserFilterDTO filterDTO, Pageable pageable);
}
