package mkoner.ads_dental_surgeries.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentResponseDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistRequestDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistResponseDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistUpdateDTO;
import mkoner.ads_dental_surgeries.service.AppointmentService;
import mkoner.ads_dental_surgeries.service.DentistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dentists")
@RequiredArgsConstructor
public class DentistController {

    private final DentistService dentistService;
    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<DentistResponseDTO> createDentist(@Valid @RequestBody DentistRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dentistService.saveDentist(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.userId or hasRole('OFFICE-MANAGER')")
    public ResponseEntity<DentistResponseDTO> getDentist(@PathVariable Long id) {
        return ResponseEntity.ok(dentistService.getDentistById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<List<DentistResponseDTO>> getAllDentists() {
        return ResponseEntity.ok(dentistService.getAllDentists());
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.userId or hasRole('OFFICE-MANAGER')")
    public ResponseEntity<DentistResponseDTO> updateDentist(@PathVariable Long id, @Valid @RequestBody DentistUpdateDTO dto) {
        return ResponseEntity.ok(dentistService.updateDentist(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<Void> deleteDentist(@PathVariable Long id) {
        dentistService.deleteDentist(id);
        return ResponseEntity.noContent().build();
    }

    // Get dentist's apts
    @GetMapping("/{id}/appointments")
    @PreAuthorize("#id == authentication.principal.userId or hasRole('OFFICE-MANAGER')")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointments(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDentist(id));
    }
}

