package mkoner.ads_dental_surgeries;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.address.AddressDTO;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentRequestDTO;
import mkoner.ads_dental_surgeries.dto.bill.BillRequestDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistRequestDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientRequestDTO;
import mkoner.ads_dental_surgeries.dto.payment.PaymentRequestDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryRequestDTO;
import mkoner.ads_dental_surgeries.dto.user.UserRequestDTO;
import mkoner.ads_dental_surgeries.model.*;
import mkoner.ads_dental_surgeries.repository.DentistRepository;
import mkoner.ads_dental_surgeries.repository.PatientRepository;
import mkoner.ads_dental_surgeries.repository.RoleRepository;
import mkoner.ads_dental_surgeries.repository.SurgeryRepository;
import mkoner.ads_dental_surgeries.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
@RequiredArgsConstructor
public class AdsDentalSurgeriesApplication{

    public static void main(String[] args) {
        SpringApplication.run(AdsDentalSurgeriesApplication.class, args);
    }

}
