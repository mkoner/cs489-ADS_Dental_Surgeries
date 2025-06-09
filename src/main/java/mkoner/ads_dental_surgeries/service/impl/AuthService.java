package mkoner.ads_dental_surgeries.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mkoner.ads_dental_surgeries.config.JwtUtil;
import mkoner.ads_dental_surgeries.dto.auth.LoginRequest;
import mkoner.ads_dental_surgeries.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public String authenticate(LoginRequest loginRequest) {
        log.info("Attempting authentication for username: {}", loginRequest.userName());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.userName(),
                            loginRequest.password()
                    )
            );

            var user = (User) authentication.getPrincipal();
            String token = jwtUtil.generateToken(user);

            log.info("Authentication successful for username: {}", loginRequest.userName());
            return token;

        } catch (Exception ex) {
            log.warn("Authentication failed for username: {}", loginRequest.userName());
            throw ex;
        }
    }
}
