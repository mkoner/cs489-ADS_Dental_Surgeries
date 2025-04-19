package mkoner.ads_dental_surgeries.dto.bill;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mkoner.ads_dental_surgeries.model.Money;
import mkoner.ads_dental_surgeries.validation.ValidCurrency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BillRequestDTO(
        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than zero")
        BigDecimal amount,
        @NotBlank(message = "currency is required")
        @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
        @ValidCurrency
        String currency,
        @NotBlank(message = "Currency symbol is required")
        @Size(min = 1, max = 3, message = "Currency symbol must be 1 to 3 characters")
        String currencySymbol,
        @NotNull(message = "due date is required")
        LocalDate dueDate
) {
}
