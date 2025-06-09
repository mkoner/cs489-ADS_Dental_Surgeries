package mkoner.ads_dental_surgeries.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mkoner.ads_dental_surgeries.dto.MessageResponseDTO;
import mkoner.ads_dental_surgeries.dto.auth.LoginRequest;
import mkoner.ads_dental_surgeries.dto.dentist.DentistRequestDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistResponseDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientRequestDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientResponseDTO;
import mkoner.ads_dental_surgeries.service.DentistService;
import mkoner.ads_dental_surgeries.service.PatientService;
import mkoner.ads_dental_surgeries.service.impl.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@SecurityRequirements(value = {})
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final DentistService dentistService;
    private final PatientService patientService;

    @Operation(
            summary = "User Login",
            description = "Upon successful login return token in response header: X-Auth-Token"
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody(
                    description = "Login credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest loginRequest) {
        log.info("Request to login: {}", loginRequest.userName());
        String token = authService.authenticate(loginRequest);
        log.info("Successfully logged in: {}", loginRequest.userName());
        return ResponseEntity.ok()
                .header("X-Auth-Token", token)
                .body(new MessageResponseDTO("Login successful"));
    }


    @Operation(
            summary = "register a new dentist",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Dentist created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping("/dentists/register")
    public ResponseEntity<DentistResponseDTO> registerDentist(
            @RequestBody(
                    description = "dentist details to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DentistRequestDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody DentistRequestDTO dentistRequestDTO) {
        log.info("Request to create dentist: {}", dentistRequestDTO);
        DentistResponseDTO dentist = dentistService.saveDentist(dentistRequestDTO);
        log.info("Successfully created dentist with id {}", dentist.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dentist);
    }

    @Operation(
            summary = "register a new patient",
            responses = {
                    @ApiResponse(responseCode = "201", description = "patient created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping("/patients/register")
    public ResponseEntity<PatientResponseDTO> registerPatient(
            @RequestBody(
                    description = "patient details to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PatientRequestDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody PatientRequestDTO patientRequestDTO) {
        log.info("Request to create patient: {}", patientRequestDTO);
        PatientResponseDTO patient = patientService.savePatient(patientRequestDTO);
        log.info("Successfully created patient with id {}", patient.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }

}
