package mkoner.ads_dental_surgeries.service.impl;

import mkoner.ads_dental_surgeries.model.Payment;
import mkoner.ads_dental_surgeries.repository.PaymentRepository;
import mkoner.ads_dental_surgeries.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public List<Payment> getPaymentsByBill(Long billId) {
        return paymentRepository.findByBillBillId(billId);
    }
}

