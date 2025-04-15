package mkoner.ads_dental_surgeries.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
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
}

