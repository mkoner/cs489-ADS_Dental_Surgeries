package mkoner.ads_dental_surgeries.exception.domain_exception;

public class InvalidCancellationStatus extends DomainException {
    public InvalidCancellationStatus(String message) {
        super("Appointment with status: " + message + " cannot be cancelled");
    }
}
