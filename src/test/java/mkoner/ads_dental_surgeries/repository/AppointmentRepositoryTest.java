package mkoner.ads_dental_surgeries.repository;

import mkoner.ads_dental_surgeries.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DentistRepository dentistRepository;

    @Autowired
    private SurgeryRepository surgeryRepository;

    private Patient patient;
    private Dentist dentist;
    private Surgery surgery;

    @BeforeEach
    void setUp() {
        Address address = new Address("USA", "NY", "10001", "Broadway");
        surgery = new Surgery("Clinic 1", "123456789");
        surgery.setAddress(address);
        surgery = surgeryRepository.save(surgery);
        Role patientRole = new Role("Patient");
        Role dentistRole = new Role("Dentist");
        patient = new Patient("John", "Doe", "1111", "john@clinic.com", "pass", LocalDate.of(1990, 1, 1), patientRole);
        patient.setAddress(address);
        patient = patientRepository.save(patient);
        dentist = dentistRepository.save(new Dentist("Jane", "Smith", "2222", "jane@clinic.com", "pass", "Ortho", dentistRole));
    }

    @Test
    @DisplayName("Should find appointments by patient user ID")
    void testFindByPatientUserId() {
        var appointment = new Appointment(LocalDateTime.now(), AppointmentStatus.SCHEDULED, patient, dentist, surgery);
        appointmentRepository.save(appointment);

        var result = appointmentRepository.findByPatientUserId(patient.getUserId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPatient().getUserId()).isEqualTo(patient.getUserId());
    }

    @Test
    @DisplayName("Should find appointments by dentist user ID")
    void testFindByDentistUserId() {
        var appointment = new Appointment(LocalDateTime.now(), AppointmentStatus.SCHEDULED, patient, dentist, surgery);
        appointmentRepository.save(appointment);

        var result = appointmentRepository.findByDentistUserId(dentist.getUserId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDentist().getUserId()).isEqualTo(dentist.getUserId());
    }

    @Test
    @DisplayName("Should find appointments by status")
    void testFindByStatus() {
        var appointment = new Appointment(LocalDateTime.now(), AppointmentStatus.CANCELLED, patient, dentist, surgery);
        appointmentRepository.save(appointment);

        var result = appointmentRepository.findByStatus(AppointmentStatus.CANCELLED);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should count appointments for dentist in a given week")
    void testCountAppointmentsForDentistInWeek() {
        var monday = LocalDate.of(2025, 4, 14).atStartOfDay(); // Monday
        var sunday = monday.plusDays(6).withHour(23).withMinute(59);

        var appointment1 = new Appointment(monday.plusDays(1).withHour(10), AppointmentStatus.SCHEDULED, patient, dentist, surgery);
        var appointment2 = new Appointment(monday.plusDays(2).withHour(11), AppointmentStatus.SCHEDULED, patient, dentist, surgery);
        var appointmentOutsideWeek = new Appointment(monday.minusDays(1), AppointmentStatus.SCHEDULED, patient, dentist, surgery);

        appointmentRepository.saveAll(List.of(appointment1, appointment2, appointmentOutsideWeek));

        long count = appointmentRepository.countAppointmentsForDentistInWeek(
                dentist.getUserId(), monday, sunday
        );

        assertThat(count).isEqualTo(2);
    }
}
