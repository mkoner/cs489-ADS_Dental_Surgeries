package mkoner.ads_dental_surgeries.dto.payment;

import mkoner.ads_dental_surgeries.model.Money;

import java.time.LocalDateTime;

public record PaymentResponseDTO(
        Long paymentId,
        LocalDateTime dateTime,
        Money money
) {
}
