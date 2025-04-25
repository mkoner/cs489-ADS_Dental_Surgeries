package mkoner.ads_dental_surgeries.repository;

import mkoner.ads_dental_surgeries.model.Role;
import mkoner.ads_dental_surgeries.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find user by email address")
    void testFindByEmailAddress() {
        // given
        Role role = new Role("PATIENT");
        User user = new User("John", "Doe", "1234567890", "john.doe@example.com", "securePass", role);
        userRepository.save(user);

        // when
        Optional<User> result = userRepository.findByEmailAddress("john.doe@example.com");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should find user by phone number")
    void testFindByPhoneNumber() {
        // given
        Role role = new Role("DENTIST");
        User user = new User("Jane", "Smith", "9876543210", "jane.smith@example.com", "securePass", role);
        userRepository.save(user);

        // when
        Optional<User> result = userRepository.findByPhoneNumber("9876543210");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getEmailAddress()).isEqualTo("jane.smith@example.com");
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void testFindByEmailAddress_NotFound() {
        Optional<User> result = userRepository.findByEmailAddress("not.exist@example.com");
        assertThat(result).isNotPresent();
    }

    @Test
    @DisplayName("Should return empty when user not found by phone")
    void testFindByPhoneNumber_NotFound() {
        Optional<User> result = userRepository.findByPhoneNumber("0000000000");
        assertThat(result).isNotPresent();
    }
}
