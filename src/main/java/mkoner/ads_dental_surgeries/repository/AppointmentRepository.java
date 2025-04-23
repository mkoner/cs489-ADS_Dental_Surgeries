package mkoner.ads_dental_surgeries.repository;

import mkoner.ads_dental_surgeries.model.Appointment;
import mkoner.ads_dental_surgeries.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    List<Appointment> findByPatientUserId(Long patientId);
    List<Appointment> findByDentistUserId(Long dentistId);
    List<Appointment> findByStatus(AppointmentStatus status);
    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.dentist.userId = :dentistId " +
            "AND a.dateTime BETWEEN :startOfWeek AND :endOfWeek")
    long countAppointmentsForDentistInWeek(@Param("dentistId") Long dentistId,
                                           @Param("startOfWeek") LocalDateTime startOfWeek,
                                           @Param("endOfWeek") LocalDateTime endOfWeek);

}

