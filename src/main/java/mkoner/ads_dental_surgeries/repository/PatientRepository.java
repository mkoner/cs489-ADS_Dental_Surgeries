package mkoner.ads_dental_surgeries.repository;

import mkoner.ads_dental_surgeries.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}

