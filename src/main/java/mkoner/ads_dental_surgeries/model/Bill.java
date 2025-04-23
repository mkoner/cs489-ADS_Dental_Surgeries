package mkoner.ads_dental_surgeries.model;

import jakarta.persistence.*;
import lombok.Data;
import mkoner.ads_dental_surgeries.exception.custom_exception.BadRequestException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    public void makePayment(Money amountPaid) {
        if((getTotalPaid().add(amountPaid.getAmount())).compareTo(this.amount.getAmount()) > 0) {
            throw new BadRequestException("Amount cannot be greater than the balance amount");
        }
        Payment payment = new Payment();
        payment.setDateTimeOfPayment(LocalDateTime.now());
        payment.setAmount(amountPaid);
        payment.setBill(this);
        payments.add(payment);
        updatePaymentStatus();
    }
    public void updatePaymentStatus() {
        BigDecimal balance = getTotalPaid().subtract(this.amount.getAmount());

        if (balance.compareTo(BigDecimal.ZERO) == 0) {
            this.paymentStatus = PaymentStatus.PAID;
        } else if (balance.compareTo(this.amount.getAmount()) == 0) {
            this.paymentStatus = PaymentStatus.UNPAID;
        } else {
            this.paymentStatus = PaymentStatus.PARTIALLY_PAID;
        }
    }
    private BigDecimal getTotalPaid() {
        return payments.stream()
                .map(p -> p.getAmount().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}

