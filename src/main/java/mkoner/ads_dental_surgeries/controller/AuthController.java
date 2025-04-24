package mkoner.ads_dental_surgeries.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final DentistService dentistService;
    private final PatientService patientService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.authenticate(loginRequest);

        return ResponseEntity.ok()
                .header("X-Auth-Token", token)
                .body(new MessageResponseDTO("Login successful"));
    }


    @PostMapping("/dentists/register")
    public ResponseEntity<DentistResponseDTO> registerDentist(@Valid @RequestBody DentistRequestDTO dentistRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dentistService.saveDentist(dentistRequestDTO));
    }

    @PostMapping("/patients/register")
    public ResponseEntity<PatientResponseDTO> registerPatient(@Valid @RequestBody PatientRequestDTO patientRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.savePatient(patientRequestDTO));
    }

}
