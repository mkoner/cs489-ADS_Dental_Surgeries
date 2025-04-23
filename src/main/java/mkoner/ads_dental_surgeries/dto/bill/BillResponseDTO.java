package mkoner.ads_dental_surgeries.dto.bill;

import mkoner.ads_dental_surgeries.dto.money.MoneyDTO;
import mkoner.ads_dental_surgeries.dto.payment.PaymentResponseDTO;
import mkoner.ads_dental_surgeries.model.Money;

import java.time.LocalDate;
import java.util.List;

public record BillResponseDTO(
        Long billId,
        MoneyDTO money,
        LocalDate dueDate,
        LocalDate billingDate,
        String paymentStatus,
        List<PaymentResponseDTO> payments
) {
}
