package mkoner.ads_dental_surgeries.repository;

import mkoner.ads_dental_surgeries.model.Appointment;
import mkoner.ads_dental_surgeries.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientUserId(Long patientId);
    List<Appointment> findByDentistUserId(Long dentistId);
    List<Appointment> findByStatus(AppointmentStatus status);

}

