package mkoner.ads_dental_surgeries.dto.bill;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import mkoner.ads_dental_surgeries.validation.ValidCurrency;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "DTO used for creating a bill with amount, currency, and due date details")
public record BillRequestDTO(

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than zero")
        @Schema(description = "Amount to be billed", example = "150.75")
        BigDecimal amount,

        @NotBlank(message = "currency is required")
        @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
        @ValidCurrency
        @Schema(description = "Currency code in ISO 4217 format", example = "USD")
        String currency,

        @NotBlank(message = "Currency symbol is required")
        @Size(min = 1, max = 3, message = "Currency symbol must be 1 to 3 characters")
        @Schema(description = "Currency symbol", example = "$")
        String currencySymbol,

        @NotNull(message = "due date is required")
        @Schema(description = "Date when the bill is due", example = "2025-06-30")
        LocalDate dueDate

) {}
