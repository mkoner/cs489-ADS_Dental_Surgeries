package mkoner.ads_dental_surgeries.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryFilterDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryRequestDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryResponseDTO;
import mkoner.ads_dental_surgeries.service.SurgeryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/surgeries")
@RequiredArgsConstructor
@Slf4j
public class SurgeryController {

    private final SurgeryService surgeryService;

    @Operation(
            summary = "Create a new surgery",
            description = "Only users with the OFFICE-MANAGER can create a new surgery",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<SurgeryResponseDTO> createSurgery(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Surgery details to create",
                required = true,
                content = @Content(schema = @Schema(implementation = SurgeryRequestDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody SurgeryRequestDTO dto) {
        log.info("Request to create surgery {}", dto);
        var surgery = surgeryService.saveSurgery(dto);
        log.info("Successfully created surgery {}", surgery.surgeryId());
        return ResponseEntity.status(HttpStatus.CREATED).body(surgery);
    }


    @Operation(
            summary = "Get a surgery by ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<SurgeryResponseDTO> getSurgery(
            @Parameter(description = "ID of the surgery to retrieve", required = true)
            @PathVariable Long id) {
        log.info("Request to get surgery {}", id);
        var surgery = surgeryService.getSurgeryById(id);
        log.info("Successfully fetched surgery {}", surgery.surgeryId());
        return ResponseEntity.ok(surgery);
    }


    @Operation(
            summary = "Get surgeries with optional filters and pagination",
            description = "Set `fetchAll=true` to ignore pagination and return all surgeries"
    )
    @GetMapping
    public ResponseEntity<?> getSurgeries(
            @Parameter(description = "Whether to fetch all surgeries without pagination", example = "false")
            @RequestParam(value = "fetchAll", required = false, defaultValue = "false") boolean fetchAll,
            @Parameter(description = "Filter criteria for surgeries", required = false)
            @ModelAttribute SurgeryFilterDTO filterDTO,
            @Parameter(description = "Pagination and sorting criteria", required = false)
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        if (fetchAll) {
            log.info("Request to fetch all surgeries without pagination");
            List<SurgeryResponseDTO> surgeries = surgeryService.getAllSurgeries();
            return ResponseEntity.ok(surgeries);
        } else {
            log.info("Request to fetch surgeries with pagination and filter {} {}", filterDTO, pageable);
            Page<SurgeryResponseDTO> filteredSurgeries = surgeryService.getFilteredSurgeriesWithPagination(filterDTO, pageable);
            return ResponseEntity.ok(filteredSurgeries);
        }
    }



    @Operation(
            summary = "Update an existing Surgery",
            description = "Updates surgery details by ID, only Office Manager can perform this action",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return updated surgery"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<SurgeryResponseDTO> updateSurgery(
            @Parameter(description = "ID of the surgery to update", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated surgery details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SurgeryRequestDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody SurgeryRequestDTO dto) {
        log.info("Request to update surgery {} {}", id, dto);
        var surgery = surgeryService.updateSurgery(id, dto);
        log.info("Successfully updated surgery {}", surgery.surgeryId());
        return ResponseEntity.ok(surgery);
    }

    @Operation(
            summary = "Delete a surgery",
            description = "Delete surgery, throw exception if user has an associated appointment, only OFFICE MANAGER can perform this action",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Surgery deleted"),
                    @ApiResponse(responseCode = "400", description = "Invalid input, surgery has associated appointments"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<Void> deleteSurgery(
            @Parameter(description = "ID of the surgery to delete", required = true)
            @PathVariable Long id) {
        log.info("Request to delete surgery {}", id);
        surgeryService.deleteSurgery(id);
        log.info("Successfully deleted surgery {}", id);
        return ResponseEntity.noContent().build();
    }

}

