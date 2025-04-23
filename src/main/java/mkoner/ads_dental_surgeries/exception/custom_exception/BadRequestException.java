package mkoner.ads_dental_surgeries.exception.custom_exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
