package mkoner.ads_dental_surgeries.repository;

import mkoner.ads_dental_surgeries.model.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DentistRepository extends JpaRepository<Dentist, Long> {
    List<Dentist> findBySpecializationContainingIgnoreCase(String specialization);
}

