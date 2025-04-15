package mkoner.ads_dental_surgeries;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.model.*;
import mkoner.ads_dental_surgeries.repository.DentistRepository;
import mkoner.ads_dental_surgeries.repository.PatientRepository;
import mkoner.ads_dental_surgeries.repository.RoleRepository;
import mkoner.ads_dental_surgeries.repository.SurgeryRepository;
import mkoner.ads_dental_surgeries.service.AppointmentService;
import mkoner.ads_dental_surgeries.service.DentistService;
import mkoner.ads_dental_surgeries.service.PatientService;
import mkoner.ads_dental_surgeries.service.SurgeryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
@RequiredArgsConstructor
public class AdsDentalSurgeriesApplication implements CommandLineRunner {

    private final SurgeryService surgeryService;
    private final DentistService dentistService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(AdsDentalSurgeriesApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Surgery s10 = new Surgery("S10", "1234567890");
        Surgery s13 = new Surgery("S13", "1234567891");
        Surgery s15 = new Surgery("S15", "1234567892");
        surgeryService.saveSurgery(s10);
        surgeryService.saveSurgery(s13);
        surgeryService.saveSurgery(s15);

        // Roles
        Role role1 = new Role("Patient");
        Role role2 = new Role("Dentist");

        // Create dentists
        Dentist tony = new Dentist("Tony", "Smith", "1111", "tony@clinic.com", "pass", "Oral Health", role2);
        Dentist helen = new Dentist("Helen", "Pearson", "1112", "helen@clinic.com", "pass", "Orthodontics", role2);
        Dentist robin = new Dentist("Robin", "Plevin", "1113", "robin@clinic.com", "pass", "Cosmetic Dentistry", role2);
        dentistService.saveDentist(tony);
        dentistService.saveDentist(helen);
        dentistService.saveDentist(robin);

        // Create patients
        Patient gillian = new Patient("Gillian", "White", "2221", "gillian@clinic.com", "pass", LocalDate.of(1990, 5, 10), role1);
        Patient jill = new Patient("Jill", "Bell", "2222", "jill@clinic.com", "pass", LocalDate.of(1985, 2, 12), role1);
        Patient ian = new Patient("Ian", "MacKay", "2223", "ian@clinic.com", "pass", LocalDate.of(1978, 8, 19), role1);
        Patient john = new Patient("John", "Walker", "2224", "john@clinic.com", "pass", LocalDate.of(1992, 1, 25), role1);
        patientService.savePatient(gillian);
        patientService.savePatient(jill);
        patientService.savePatient(ian);
        patientService.savePatient(john);

        // Appointments
        appointmentService.saveAppointment(new Appointment(LocalDateTime.of(2013, 9, 12, 10, 0), AppointmentStatus.SCHEDULED, gillian, tony, s15));
        appointmentService.saveAppointment(new Appointment(LocalDateTime.of(2013, 9, 12, 12, 0), AppointmentStatus.SCHEDULED, jill, tony, s15));
        appointmentService.saveAppointment(new Appointment(LocalDateTime.of(2013, 9, 12, 10, 0), AppointmentStatus.SCHEDULED, ian, helen, s10));
        appointmentService.saveAppointment(new Appointment(LocalDateTime.of(2013, 9, 14, 14, 0), AppointmentStatus.SCHEDULED, ian, helen, s10));
        appointmentService.saveAppointment(new Appointment(LocalDateTime.of(2013, 9, 14, 16, 30), AppointmentStatus.SCHEDULED, jill, robin, s15));
        appointmentService.saveAppointment(new Appointment(LocalDateTime.of(2013, 9, 15, 18, 0), AppointmentStatus.SCHEDULED, john, robin, s13));
    }
}
