package mkoner.ads_dental_surgeries.dto.appointment;

import mkoner.ads_dental_surgeries.model.AppointmentStatus;
import mkoner.ads_dental_surgeries.model.PaymentStatus;

import java.time.LocalDate;

public record AppointmentFilterDTO(
        LocalDate appointmentDate,
        AppointmentStatus status,
        String patientEmail,
        String dentistEmail,
        String surgeryCountry,
        String surgeryCity,
        PaymentStatus paymentStatus
) {}

