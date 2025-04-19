package mkoner.ads_dental_surgeries.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private LocalDateTime dateTimeOfPayment;

    @Embedded
    private Money amount;

    @ManyToOne
    @JoinColumn(name = "bill_id")
    private Bill bill;

    public Payment(Money amount, Bill bill) {
        if(bill == null){
            throw new IllegalArgumentException("bill is null");
        }
        this.amount = amount;
        this.bill = bill;
        this.dateTimeOfPayment = LocalDateTime.now();
    }
}

