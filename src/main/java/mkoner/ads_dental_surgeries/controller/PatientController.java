package mkoner.ads_dental_surgeries.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentResponseDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientFilterDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientRequestDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientResponseDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientUpdateDTO;
import mkoner.ads_dental_surgeries.service.AppointmentService;
import mkoner.ads_dental_surgeries.service.PatientService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    @Operation(
            summary = "Create a new Patient",
            description = "Only Office manager can perform this action",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Patient created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<PatientResponseDTO> createPatient(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Patient details to create",
                required = true,
                content = @Content(schema = @Schema(implementation = PatientRequestDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody PatientRequestDTO dto) {
        log.info("Request to create patient: {}", dto);
        PatientResponseDTO patient = patientService.savePatient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }

    @Operation(
            summary = "Get a Patient by ID",
            description = "Only accessible to OFFICE-MANAGER and the Patient himself"
    )
    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.userId or hasRole('OFFICE-MANAGER')")
    public ResponseEntity<PatientResponseDTO> getPatient(
            @Parameter(description = "ID of the patient to retrieve", required = true)
            @PathVariable Long id) {
        log.info("Request to get Patient {}", id);
        PatientResponseDTO patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    @Operation(
            summary = "Get Patients with optional filters and pagination",
            description = "Set `fetchAll=true` to ignore pagination and return all patients",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return patients"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<?> getPatients(
            @Parameter(description = "Filter criteria for patients", required = false)
            @ModelAttribute PatientFilterDTO filterDTO,
            @Parameter(description = "Pagination and sorting criteria", required = false)
            @PageableDefault(size = 10, sort = "firstName") Pageable pageable,
            @Parameter(description = "Whether to fetch all patients without pagination", example = "false")
            @RequestParam(defaultValue = "false") boolean fetchAll) {
        if(fetchAll) {
            log.info("Request to fetch all patients");
            return ResponseEntity.ok(patientService.getAllPatients());
        }
        else{
            log.info("Request to fetch patients with optional filters and pagination: {} {}", filterDTO, pageable);
            return ResponseEntity.ok(patientService.getFilteredPatientsWithPagination(filterDTO, pageable));
        }
    }


    @Operation(
            summary = "Update an existing patient",
            description = "Updates patient details by ID, only Office Manager and the patient can perform this action",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return updated patient"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.userId or hasRole('OFFICE-MANAGER')")
    public ResponseEntity<PatientResponseDTO> updatePatient(
            @Parameter(description = "ID of the patient to update", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated patient details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PatientUpdateDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody PatientUpdateDTO dto) {
        log.info("Request to update patient {} {}", id, dto);
        PatientResponseDTO patient = patientService.updatePatient(id, dto);
        log.info("Successfully updated patient {}", id);
        return ResponseEntity.ok(patient);
    }

    @Operation(
            summary = "Delete a patient",
            description = "Delete patient, throw exception if patient has an associated appointment, only OFFICE MANAGER can perform this action",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Patient deleted"),
                    @ApiResponse(responseCode = "400", description = "Invalid input, patient has associated appointments"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<Void> deletePatient(
            @Parameter(description = "ID of the patient to delete", required = true)
            @PathVariable Long id) {
        log.info("Request to delete patient {}", id);
        patientService.deletePatient(id);
        log.info("Successfully deleted patient {}", id);
        return ResponseEntity.noContent().build();
    }

    // Get patient's appointments
    @Operation(
            summary = "Get patient's appointments"
    )
    @GetMapping("/{id}/appointments")
    @PreAuthorize("#id == authentication.principal.userId or hasRole('OFFICE-MANAGER')")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointments(
            @Parameter(description = "ID of the patient", required = true)
            @PathVariable Long id) {
        log.info("Request to get appointments for patient {}", id);
        var appointments = appointmentService.getAppointmentsByPatient(id);
        return ResponseEntity.ok(appointments);
    }
}

