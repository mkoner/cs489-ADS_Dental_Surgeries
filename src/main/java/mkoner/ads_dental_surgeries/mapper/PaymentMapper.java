package mkoner.ads_dental_surgeries.mapper;

import mkoner.ads_dental_surgeries.dto.payment.PaymentRequestDTO;
import mkoner.ads_dental_surgeries.dto.payment.PaymentResponseDTO;
import mkoner.ads_dental_surgeries.model.Money;
import mkoner.ads_dental_surgeries.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public PaymentResponseDTO mapToPaymentResponseDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getPaymentId(),
                payment.getDateTimeOfPayment(),
                payment.getAmount()
        );
    }
}
