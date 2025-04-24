package mkoner.ads_dental_surgeries.dto.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RescheduleAppointmentDTO(
        @NotNull @Future(message = "Rescheduled date must be in the future")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime newDateTime
) {}

