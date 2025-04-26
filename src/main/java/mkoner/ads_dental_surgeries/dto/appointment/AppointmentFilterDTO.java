package mkoner.ads_dental_surgeries.dto.appointment;

import io.swagger.v3.oas.annotations.media.Schema;
import mkoner.ads_dental_surgeries.model.AppointmentStatus;
import mkoner.ads_dental_surgeries.model.PaymentStatus;

import java.time.LocalDate;

@Schema(description = "DTO used to filter appointments based on various criteria")
public record AppointmentFilterDTO(

        @Schema(description = "Date of the appointment", example = "2025-05-10")
        LocalDate appointmentDate,

        @Schema(description = "Status of the appointment", example = "CONFIRMED")
        AppointmentStatus status,

        @Schema(description = "Email of the patient", example = "patient@example.com")
        String patientEmail,

        @Schema(description = "Email of the dentist", example = "dentist@example.com")
        String dentistEmail,

        @Schema(description = "Country where the surgery is located", example = "United States")
        String surgeryCountry,

        @Schema(description = "City where the surgery is located", example = "Los Angeles")
        String surgeryCity,

        @Schema(description = "Payment status for the appointment", example = "PAID")
        PaymentStatus paymentStatus

) {}


