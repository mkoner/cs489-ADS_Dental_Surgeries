package mkoner.ads_dental_surgeries.dto.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import mkoner.ads_dental_surgeries.model.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentRequestDTO(
        //@NotNull(message = "Appointment date time is required")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime dateTime,
        @NotNull(message = "Patient id is required")
        Long patientId,
        Long dentistId,
        Long surgeryId,
        AppointmentStatus status
) {
}
