package mkoner.ads_dental_surgeries.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.MessageResponseDTO;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentFilterDTO;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentRequestDTO;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentResponseDTO;
import mkoner.ads_dental_surgeries.dto.appointment.RescheduleAppointmentDTO;
import mkoner.ads_dental_surgeries.dto.bill.BillRequestDTO;
import mkoner.ads_dental_surgeries.dto.bill.BillResponseDTO;
import mkoner.ads_dental_surgeries.dto.payment.PaymentRequestDTO;
import mkoner.ads_dental_surgeries.dto.payment.PaymentResponseDTO;
import mkoner.ads_dental_surgeries.model.User;
import mkoner.ads_dental_surgeries.service.AppointmentService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OFFICE-MANAGER', 'PATIENT')")
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@Valid @RequestBody AppointmentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.saveAppointment(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointment(@PathVariable Long id) {
        AppointmentResponseDTO appointment = appointmentService.getAppointmentById(id);

        User currentUser = getCurrentUser();
        boolean isPatient = appointment.patient().userId().equals(currentUser.getUserId());
        boolean isDentist = appointment.dentist().userId().equals(currentUser.getUserId());

        if (!isOfficeManager() && !isPatient && !isDentist) {
            throw new AccessDeniedException("You are not authorized to view this appointment.");
        }

        return ResponseEntity.ok(appointment);
    }

    @GetMapping
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<?> getAppointments(
            @ModelAttribute AppointmentFilterDTO filterDTO,
            @RequestParam(defaultValue = "false") boolean fetchAll,
            @PageableDefault(size = 10, sort = "dateTime") Pageable pageable
    ) {
        if(fetchAll) {
            return ResponseEntity.ok(appointmentService.getAllAppointments());
        }
        return ResponseEntity.ok(appointmentService.getFilteredAppointments(filterDTO, pageable));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(@PathVariable Long id,
                                                            @Valid @RequestBody AppointmentRequestDTO dto) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, dto));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('OFFICE-MANAGER', 'PATIENT')")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {
        String message = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(new MessageResponseDTO(message));
    }

    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('OFFICE-MANAGER', 'PATIENT')")
    public ResponseEntity<AppointmentResponseDTO> rescheduleAppointment(@PathVariable Long id,
                                                                    @Valid @RequestBody RescheduleAppointmentDTO dto) {
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(id, dto));
    }

    // Generate bill
    @PostMapping("/{id}/bills")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<BillResponseDTO> generateBill(@PathVariable Long id, @Valid @RequestBody BillRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.generateBill(id, dto));
    }

    //Make a payment
    @PostMapping("/{id}/payments")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<PaymentResponseDTO> registerPayment(@PathVariable Long id,@Valid @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.makePayment(id, dto));
    }

    private boolean isOfficeManager() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OFFICE-MANAGER"));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

}

