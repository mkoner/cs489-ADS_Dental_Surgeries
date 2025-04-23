package mkoner.ads_dental_surgeries.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class SurgeryController {

    private final SurgeryService surgeryService;

    @PostMapping
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<SurgeryResponseDTO> createSurgery(@Valid @RequestBody SurgeryRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(surgeryService.saveSurgery(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurgeryResponseDTO> getSurgery(@PathVariable Long id) {
        return ResponseEntity.ok(surgeryService.getSurgeryById(id));
    }

    @GetMapping
    public ResponseEntity<?> getSurgeries(
            @RequestParam(value = "fetchAll", required = false, defaultValue = "false") boolean fetchAll,
            @ModelAttribute SurgeryFilterDTO filterDTO,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        if (fetchAll) {
            List<SurgeryResponseDTO> surgeries = surgeryService.getAllSurgeries();
            return ResponseEntity.ok(surgeries);
        } else {
            Page<SurgeryResponseDTO> filteredSurgeries = surgeryService.getFilteredSurgeriesWithPagination(filterDTO, pageable);
            return ResponseEntity.ok(filteredSurgeries);
        }
    }



    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<SurgeryResponseDTO> updateSurgery(@PathVariable Long id, @Valid @RequestBody SurgeryRequestDTO dto) {
        return ResponseEntity.ok(surgeryService.updateSurgery(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<Void> deleteSurgery(@PathVariable Long id) {
        surgeryService.deleteSurgery(id);
        return ResponseEntity.noContent().build();
    }

}

