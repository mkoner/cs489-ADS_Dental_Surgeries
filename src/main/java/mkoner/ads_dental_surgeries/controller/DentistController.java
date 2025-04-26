package mkoner.ads_dental_surgeries.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentResponseDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistFilterDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistRequestDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistResponseDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistUpdateDTO;
import mkoner.ads_dental_surgeries.service.AppointmentService;
import mkoner.ads_dental_surgeries.service.DentistService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @Operation(
            summary = "Create a new Dentist",
            description = "Only Office manager can perform this action",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Dentist created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<DentistResponseDTO> createDentist(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dentist details to create",
                required = true,
                content = @Content(schema = @Schema(implementation = DentistRequestDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody DentistRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dentistService.saveDentist(dto));
    }

    @Operation(
            summary = "Get a Dentist by ID",
            description = "Only accessible to OFFICE-MANAGER and the Dentist himself",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return dentist"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.userId or hasRole('OFFICE-MANAGER')")
    public ResponseEntity<DentistResponseDTO> getDentist(
            @Parameter(description = "ID of the dentist to retrieve", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(dentistService.getDentistById(id));
    }

    @Operation(
            summary = "Get Dentists with optional filters and pagination",
            description = "Set `fetchAll=true` to ignore pagination and return all dentists",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return dentist"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<?> getDentists(
            @Parameter(description = "Filter criteria for dentists", required = false)
            @ModelAttribute DentistFilterDTO filterDTO,
            @Parameter(description = "Whether to fetch all dentist without pagination", example = "false")
            @RequestParam(required = false, defaultValue = "false") boolean fetchAll,
            @Parameter(description = "Pagination and sorting criteria", required = false)
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable
    ) {
        if(fetchAll) {
            return ResponseEntity.ok(dentistService.getAllDentists());
        }
        return ResponseEntity.ok(dentistService.getFilteredDentists(filterDTO, pageable));
    }


    @Operation(
            summary = "Update an existing dentist",
            description = "Updates dentist details by ID, only Office Manager and the dentist can perform this action",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return updated dentist"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.userId or hasRole('OFFICE-MANAGER')")
    public ResponseEntity<DentistResponseDTO> updateDentist(
            @Parameter(description = "ID of the dentist to update", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated dentist details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DentistUpdateDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody DentistUpdateDTO dto) {
        return ResponseEntity.ok(dentistService.updateDentist(id, dto));
    }

    @Operation(
            summary = "Delete a dentist",
            description = "Delete dentist, throw exception if dentist has an associated appointment, only OFFICE MANAGER can perform this action",
            responses = {
                    @ApiResponse(responseCode = "204", description = "dentist deleted"),
                    @ApiResponse(responseCode = "400", description = "Invalid input, dentist has associated appointments"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<Void> deleteDentist(
            @Parameter(description = "ID of the dentist to delete", required = true)
            @PathVariable Long id) {
        dentistService.deleteDentist(id);
        return ResponseEntity.noContent().build();
    }

    // Get dentist's apts
    @Operation(
            summary = "Get dentist's appointments"
    )
    @GetMapping("/{id}/appointments")
    @PreAuthorize("#id == authentication.principal.userId or hasRole('OFFICE-MANAGER')")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointments(
            @Parameter(description = "ID of the dentist", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDentist(id));
    }
}

