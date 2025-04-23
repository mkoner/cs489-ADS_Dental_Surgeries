package mkoner.ads_dental_surgeries.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryRequestDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryResponseDTO;
import mkoner.ads_dental_surgeries.service.SurgeryService;
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
    public ResponseEntity<List<SurgeryResponseDTO>> getAllSurgeries() {
        return ResponseEntity.ok(surgeryService.getAllSurgeries());
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

