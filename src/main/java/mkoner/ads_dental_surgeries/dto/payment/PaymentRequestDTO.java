package mkoner.ads_dental_surgeries.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public record PaymentRequestDTO(
        @NotNull(message = "payment amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than zero")
        BigDecimal amount
) {
}
