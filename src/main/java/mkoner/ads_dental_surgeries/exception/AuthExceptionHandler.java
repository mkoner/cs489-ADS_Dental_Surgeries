package mkoner.ads_dental_surgeries.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.time.Instant;

@RestControllerAdvice
@Order(2)
public class AuthExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return buildError(HttpStatus.UNAUTHORIZED, "Login Failed", "Invalid username or password");
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledAccount(DisabledException ex) {
        return buildError(HttpStatus.UNAUTHORIZED, "Login Failed", "Account has been disabled");
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLockedAccount(LockedException ex) {
        return buildError(HttpStatus.UNAUTHORIZED, "Login Failed", "Account has been locked");
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAccessDenied(Exception ex) {
        return buildError(HttpStatus.FORBIDDEN, "Access Denied", "You are not authorized to perform this action");
    }


    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
        return buildError(HttpStatus.UNAUTHORIZED, "Token Expired", "Your session has expired. Please log in again.");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleLockedAccount(AuthenticationException ex) {
        return buildError(HttpStatus.UNAUTHORIZED, "Login Failed", "Authentication Failed");
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(
                Instant.now(),
                status.value(),
                error,
                message
        ));
    }
}

