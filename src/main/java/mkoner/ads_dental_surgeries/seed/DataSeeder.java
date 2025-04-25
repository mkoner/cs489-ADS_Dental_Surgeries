package mkoner.ads_dental_surgeries.seed;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.address.AddressDTO;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentRequestDTO;
import mkoner.ads_dental_surgeries.dto.bill.BillRequestDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistRequestDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientRequestDTO;
import mkoner.ads_dental_surgeries.dto.payment.PaymentRequestDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryRequestDTO;
import mkoner.ads_dental_surgeries.dto.user.UserRequestDTO;
import mkoner.ads_dental_surgeries.model.AppointmentStatus;
import mkoner.ads_dental_surgeries.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true")
public class DataSeeder implements CommandLineRunner {
    private final UserService userService;
    private final SurgeryService surgeryService;
    private final DentistService dentistService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    @Override
    public void run(String... args) throws Exception {
        var user = new UserRequestDTO("Admin", "Admin1", "1111765", "admin1@clinic.com", "pass", "OFFICE-MANAGER");
        userService.createUser(user);

        var address1 = new AddressDTO("United States", "New York", "54332", "New York ST");
        var address2 = new AddressDTO("United States", "Fairfield", "5555", "fairfield ST");
        var address3 = new AddressDTO("Germany", "Munich", "12345", "Munich St");
        var s10 = new SurgeryRequestDTO("S10", "1234567890", address1);
        var s13 = new SurgeryRequestDTO("S13", "1234567891", address2);
        var s15 = new SurgeryRequestDTO("S15", "1234567892", address3);
        var s10Response = surgeryService.saveSurgery(s10);
        var s13Response = surgeryService.saveSurgery(s13);
        var s15Response = surgeryService.saveSurgery(s15);



        // Create dentists
        var tony = new DentistRequestDTO("Tony", "Smith", "1111", "tony@clinic.com", "pass", "Oral Health");
        var helen = new DentistRequestDTO("Helen", "Pearson", "1112", "helen@clinic.com", "pass", "Orthodontics");
        var robin = new DentistRequestDTO("Robin", "Plevin", "1113", "robin@clinic.com", "pass", "Cosmetic Dentistry");
        var tonyResponse = dentistService.saveDentist(tony);
        var helenResponse = dentistService.saveDentist(helen);
        var robinResponse = dentistService.saveDentist(robin);

        // Create patients
        var gillian = new PatientRequestDTO("Gillian", "White", "2221", "gillian@clinic.com", "pass", LocalDate.of(1990, 5, 10), address1);
        var jill = new PatientRequestDTO("Jill", "Bell", "2222", "jill@clinic.com", "pass", LocalDate.of(1985, 2, 12), address1);
        var ian = new PatientRequestDTO("Ian", "MacKay", "2223", "ian@clinic.com", "pass", LocalDate.of(1978, 8, 19), address2);
        var john = new PatientRequestDTO("John", "Walker", "2224", "john@clinic.com", "pass", LocalDate.of(1992, 1, 25), address3);
        var gillianResponse =  patientService.savePatient(gillian);
        var jillResponse =  patientService.savePatient(jill);
        var ianResponse = patientService.savePatient(ian);
        var johnResponse = patientService.savePatient(john);

//        System.out.println(s10Response);
//        System.out.println(s13Response);
//        System.out.println(s15Response);
//        System.out.println(ianResponse);
        // Appointments

        var apt1 = appointmentService.saveAppointment(new AppointmentRequestDTO(LocalDateTime.of(2013, 9, 12, 10, 0), gillianResponse.userId(), tonyResponse.userId(), s15Response.surgeryId(), AppointmentStatus.SCHEDULED));
        appointmentService.saveAppointment(new AppointmentRequestDTO(LocalDateTime.of(2013, 9, 12, 12, 0), jillResponse.userId(), tonyResponse.userId(), s15Response.surgeryId(), AppointmentStatus.SCHEDULED));
        appointmentService.saveAppointment(new AppointmentRequestDTO(LocalDateTime.of(2013, 9, 12, 10, 0), ianResponse.userId(), helenResponse.userId(), s10Response.surgeryId(), AppointmentStatus.SCHEDULED));
        appointmentService.saveAppointment(new AppointmentRequestDTO(LocalDateTime.of(2013, 9, 14, 14, 0), ianResponse.userId(), helenResponse.userId(), s10Response.surgeryId(), AppointmentStatus.SCHEDULED));
        appointmentService.saveAppointment(new AppointmentRequestDTO(LocalDateTime.of(2013, 9, 10, 16, 30), jillResponse.userId(), robinResponse.userId(), s15Response.surgeryId(), AppointmentStatus.SCHEDULED));
        appointmentService.saveAppointment(new AppointmentRequestDTO(LocalDateTime.of(2013, 9, 15, 18, 0), johnResponse.userId(), robinResponse.userId(), s13Response.surgeryId(), AppointmentStatus.SCHEDULED));
        appointmentService.saveAppointment(new AppointmentRequestDTO(LocalDateTime.of(2013, 9, 12, 10, 0), ianResponse.userId(), robinResponse.userId(), s13Response.surgeryId(), AppointmentStatus.SCHEDULED));
        appointmentService.saveAppointment(new AppointmentRequestDTO(LocalDateTime.of(2013, 9, 13, 19, 0), johnResponse.userId(), robinResponse.userId(), s13Response.surgeryId(), AppointmentStatus.SCHEDULED));
        appointmentService.saveAppointment(new AppointmentRequestDTO(LocalDateTime.of(2013, 9, 14, 18, 0), jillResponse.userId(), robinResponse.userId(), s13Response.surgeryId(), AppointmentStatus.SCHEDULED));

        System.out.println(apt1);
        var bill = appointmentService.generateBill(apt1.appointmentId(), new BillRequestDTO(new BigDecimal("234.85"), "USD", "$", LocalDate.of(2021, 7, 12)));
        System.out.println(bill);

        var payment = appointmentService.makePayment(apt1.appointmentId(), new PaymentRequestDTO(new BigDecimal("104.85")));
        System.out.println(payment);
    }
}
