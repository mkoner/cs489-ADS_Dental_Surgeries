package mkoner.ads_dental_surgeries.repository;

import mkoner.ads_dental_surgeries.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBillBillId(Long billId);
}

