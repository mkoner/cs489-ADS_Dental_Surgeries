package mkoner.ads_dental_surgeries.service.impl;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.payment.PaymentRequestDTO;
import mkoner.ads_dental_surgeries.dto.payment.PaymentResponseDTO;
import mkoner.ads_dental_surgeries.mapper.PaymentMapper;
import mkoner.ads_dental_surgeries.model.Payment;
import mkoner.ads_dental_surgeries.repository.PaymentRepository;
import mkoner.ads_dental_surgeries.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::mapToPaymentResponseDTO)
                .toList();
    }

    public PaymentResponseDTO getPaymentById(Long id) {
        var payment = paymentRepository.findById(id).orElse(null);
        return paymentMapper.mapToPaymentResponseDTO(payment);
    }

//    public PaymentResponseDTO savePayment(PaymentRequestDTO payment) {
//        var newPayment = paymentMapper.mapToPayment(payment);
//        return paymentMapper.mapToPaymentResponseDTO(paymentRepository.save(newPayment));
//    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public List<PaymentResponseDTO> getPaymentsByBill(Long billId) {
        return paymentRepository.findByBillBillId(billId).stream()
                .map(paymentMapper::mapToPaymentResponseDTO).toList();
    }
}

