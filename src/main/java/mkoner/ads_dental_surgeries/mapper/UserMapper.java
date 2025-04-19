package mkoner.ads_dental_surgeries.mapper;

import mkoner.ads_dental_surgeries.dto.user.UserRequestDTO;
import mkoner.ads_dental_surgeries.dto.user.UserResponseDTO;
import mkoner.ads_dental_surgeries.model.Role;
import mkoner.ads_dental_surgeries.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User mapToUser(UserRequestDTO userRequestDTO) {
        return new User(userRequestDTO.firstName(), userRequestDTO.lastName(),
                userRequestDTO.phoneNumber(), userRequestDTO.email(),
                userRequestDTO.password(), new Role(userRequestDTO.role()));
    }
    public UserResponseDTO mapToUserResponseDTO(User user) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getEmailAddress()
        );
    }
}
