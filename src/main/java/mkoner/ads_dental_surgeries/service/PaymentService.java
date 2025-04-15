package mkoner.ads_dental_surgeries.service;

import mkoner.ads_dental_surgeries.model.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> getAllPayments();
    Payment getPaymentById(Long id);
    Payment savePayment(Payment payment);
    void deletePayment(Long id);
    List<Payment> getPaymentsByBill(Long billId);
}

