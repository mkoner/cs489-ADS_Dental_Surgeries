package mkoner.ads_dental_surgeries.dto.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RescheduleAppointmentDTO(
        @NotNull @Future(message = "Rescheduled date must be in the future")
        LocalDateTime newDateTime
) {}

