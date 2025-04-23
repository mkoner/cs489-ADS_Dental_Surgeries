package mkoner.ads_dental_surgeries.exception.custom_exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
