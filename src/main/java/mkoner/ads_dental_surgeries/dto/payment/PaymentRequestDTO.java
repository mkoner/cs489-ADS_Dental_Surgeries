package mkoner.ads_dental_surgeries.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO used to request a payment with the specified amount")
public record PaymentRequestDTO(

        @NotNull(message = "payment amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than zero")
        @Schema(description = "Amount to be paid", example = "99.99")
        BigDecimal amount

) {}

