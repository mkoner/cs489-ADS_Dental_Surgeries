package mkoner.ads_dental_surgeries.repository;

import mkoner.ads_dental_surgeries.model.Surgery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SurgeryRepository extends JpaRepository<Surgery, Long> {
    List<Surgery> findSurgeriesByAddressCityIgnoreCase(String city);
    Optional<Surgery> findByPhoneNumber(String phoneNumber);
}

