package mkoner.ads_dental_surgeries.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import mkoner.ads_dental_surgeries.exception.domain_exception.InvalidCancellationStatus;
import mkoner.ads_dental_surgeries.exception.domain_exception.InvalidRescheduleStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;

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

    public void cancel(AppointmentStatus newStatus) {
        if(!validateCancellationStatus()){
            throw new InvalidCancellationStatus(status.name());
        }
        this.status = newStatus;
    }

    public void reschedule(LocalDateTime newDateTime, AppointmentStatus newStatus) {
        if(!validateRescheduleStatus()){
            throw new InvalidRescheduleStatus(status.name());
        }
        this.dateTime = newDateTime;
        this.status = newStatus;
    }

    public void generateBill(Money amount, LocalDate dueDate) {
        Bill bill = new Bill();
        bill.setAppointment(this);
        bill.setDateOfBilling(LocalDate.now());
        bill.setDueDate(dueDate);
        bill.setAmount(amount); // Example
        this.bill = bill;
    }

    private boolean validateRescheduleStatus() {

        EnumSet<AppointmentStatus> ALLOWED_RESCHEDULE_STATUSES = EnumSet.of(
                AppointmentStatus.REQUESTED,
                AppointmentStatus.SCHEDULED,
                AppointmentStatus.CANCELLATION_REQUESTED,
                AppointmentStatus.RESCHEDULE_REQUESTED
        );
        return ALLOWED_RESCHEDULE_STATUSES.contains(status);
    }

    private boolean validateCancellationStatus() {
        EnumSet<AppointmentStatus> ALLOWED_CANCELLATION_STATUSES = EnumSet.of(
                AppointmentStatus.REQUESTED,
                AppointmentStatus.SCHEDULED,
                AppointmentStatus.RESCHEDULE_REQUESTED,
                AppointmentStatus.RESCHEDULED
        );
        return ALLOWED_CANCELLATION_STATUSES.contains(status);
    }

}

