package mkoner.ads_dental_surgeries.exception;

import java.time.Instant;
import java.util.List;

public record ValidationErrorResponse(
        Instant timestamp,
        int status,
        String error,
        List<String> messages
) {
}
