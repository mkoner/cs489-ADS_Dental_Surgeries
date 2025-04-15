package mkoner.ads_dental_surgeries.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;


    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "dentist_id")
    private Dentist dentist;

    @ManyToOne
    @JoinColumn(name = "surgery_id")
    private Surgery surgery;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bill_id")
    private Bill bill;

    public Appointment(LocalDateTime dateTime, AppointmentStatus appointmentStatus, Patient patient, Dentist dentist, Surgery surgery) {
        this.dateTime = dateTime;
        this.status = appointmentStatus;
        this.patient = patient;
        this.dentist = dentist;
        this.surgery = surgery;
    }

    public void cancel() {
        this.status = AppointmentStatus.CANCELLED;
    }

    public void reschedule(LocalDateTime newDateTime) {
        this.dateTime = newDateTime;
        this.status = AppointmentStatus.RESCHEDULED;
    }

    public Bill generateBill(BigDecimal amount, LocalDate dueDate, String currency, String currencySymbol) {
        Bill bill = new Bill();
        bill.setAppointment(this);
        bill.setDateOfBilling(LocalDate.now());
        bill.setDueDate(dueDate);
        bill.setAmount(new Money(amount, currency, currencySymbol)); // Example
        this.bill = bill;
        return bill;
    }
}

