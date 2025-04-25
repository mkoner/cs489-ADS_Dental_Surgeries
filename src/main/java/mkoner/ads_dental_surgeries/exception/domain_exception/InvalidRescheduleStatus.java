package mkoner.ads_dental_surgeries.exception.domain_exception;

public class InvalidRescheduleStatus extends DomainException {
    public InvalidRescheduleStatus(String message) {
        super("Appointment with status: " + message + " cannot be rescheduled");
    }
}
