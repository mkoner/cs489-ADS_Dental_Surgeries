package mkoner.ads_dental_surgeries.repository;

import mkoner.ads_dental_surgeries.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmailAddress(String emailAddress);
    Optional<User> findByPhoneNumber(String phoneNumber);
}

