package mkoner.ads_dental_surgeries.repository;

import mkoner.ads_dental_surgeries.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Should save and find role by roleName")
    void shouldFindRoleByRoleName() {
        // Given
        Role role = new Role("ADMIN");
        roleRepository.save(role);

        // When
        Optional<Role> foundRole = roleRepository.findByRoleName("ADMIN");

        // Then
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getRoleName()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should return empty when role name does not exist")
    void shouldReturnEmptyForNonexistentRole() {
        Optional<Role> foundRole = roleRepository.findByRoleName("NON_EXISTENT_ROLE");

        assertThat(foundRole).isEmpty();
    }
}
