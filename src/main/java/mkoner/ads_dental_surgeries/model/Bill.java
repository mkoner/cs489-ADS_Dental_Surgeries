package mkoner.ads_dental_surgeries.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bills")
@Data
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    private LocalDate dateOfBilling;
    private LocalDate dueDate;

    @Embedded
    private Money amount;

    @OneToOne(mappedBy = "bill")
    private Appointment appointment;

    @OneToMany(mappedBy = "bill")
    private List<Payment> payments;

    public void makePayment(Money amount) {
        Payment payment = new Payment();
        payment.setDateTimeOfPayment(LocalDateTime.now());
        payment.setAmount(amount);
        payment.setBill(this);
        payments.add(payment);
    }
}

