package mkoner.ads_dental_surgeries.dto.money;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mkoner.ads_dental_surgeries.validation.ValidCurrency;

import java.math.BigDecimal;

public record MoneyDTO(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,
        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
        @ValidCurrency
        String currency,
        @NotBlank(message = "Currency symbol is required")
        @Size(min = 1, max = 3, message = "Currency symbol must be 1 to 3 characters")
        String currencySymbol
) {
}
