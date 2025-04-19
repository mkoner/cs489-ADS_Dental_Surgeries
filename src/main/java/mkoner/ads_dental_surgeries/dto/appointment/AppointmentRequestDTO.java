package mkoner.ads_dental_surgeries.dto.appointment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mkoner.ads_dental_surgeries.model.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentRequestDTO(
        //@NotNull(message = "Appointment date time is required")
        LocalDateTime dateTime,
        @NotNull(message = "Patient id is required")
        Long patientId,
        Long dentistId,
        Long surgeryId,
        AppointmentStatus status
) {
}
