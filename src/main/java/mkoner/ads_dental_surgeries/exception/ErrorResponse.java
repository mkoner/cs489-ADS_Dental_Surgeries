package mkoner.ads_dental_surgeries.exception;

import java.time.Instant;

public record ErrorResponse(
        Instant instant,
        Integer status,
        String errorType,
        String message
) {
}
