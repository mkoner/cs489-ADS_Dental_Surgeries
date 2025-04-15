package mkoner.ads_dental_surgeries.service;

import mkoner.ads_dental_surgeries.model.Role;
import mkoner.ads_dental_surgeries.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User findUserById(Long id);
    List<User> getAllUsers();
    User findByEmail(String email);
    User createUser(User user, String roleName);
    void deleteUser(Long id);
    Role createOrUpdateRole(String roleName);
    List<Role> getAllRoles();
}
