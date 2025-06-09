package mkoner.ads_dental_surgeries.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Operation(
            summary = "Create a new appointment",
            description = "Only OFFICE-MANAGER or patient role can create a new appointment",
            responses = {
                    @ApiResponse(responseCode = "201", description = "appointment created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('OFFICE-MANAGER', 'PATIENT')")
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details to create an appointment",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AppointmentRequestDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody AppointmentRequestDTO dto) {
        log.info("Request to create appointment {}", dto.toString());
        AppointmentResponseDTO createdAppointment = appointmentService.saveAppointment(dto);
        log.info("Created appointment with id {}", createdAppointment.appointmentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
    }

    @Operation(
            summary = "Get appointment details",
            description = "Only OFFICE-MANAGER or associated patient or dentist can get appointment details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "appointment details"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointment(
            @Parameter(description = "ID of the appointment ", required = true)
            @PathVariable Long id) {
        log.debug("Request to get appointment {}", id);
        AppointmentResponseDTO appointment = appointmentService.getAppointmentById(id);

        User currentUser = getCurrentUser();
        boolean isPatient = appointment.patient().userId().equals(currentUser.getUserId());
        boolean isDentist = appointment.dentist().userId().equals(currentUser.getUserId());

        if (!isOfficeManager() && !isPatient && !isDentist) {
            log.warn("User {} is not authorized to view appointment {}", currentUser.getUserId(), id);
            throw new AccessDeniedException("You are not authorized to view this appointment.");
        }
        log.info("Fetched appointment {}", id);
        return ResponseEntity.ok(appointment);
    }

    @Operation(
            summary = "Get appointments ",
            description = "Only OFFICE-MANAGER can get appointments details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "appointment details"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<?> getAppointments(
            @Parameter(description = "Filter criteria for appointments", required = false)
            @ModelAttribute AppointmentFilterDTO filterDTO,
            @Parameter(description = "Whether to fetch all appointment without pagination", example = "false")
            @RequestParam(defaultValue = "false") boolean fetchAll,
            @Parameter(description = "Pagination and sorting criteria", required = false)
            @PageableDefault(size = 10, sort = "dateTime") Pageable pageable
    ) {
        if(fetchAll) {
            log.info("Request to fetch all appointments without pagination");
            return ResponseEntity.ok(appointmentService.getAllAppointments());
        }
        log.info("Request to fetch filtered appointments with criteria: {}, page: {}, size: {}, sort: {}",
                filterDTO, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        return ResponseEntity.ok(appointmentService.getFilteredAppointments(filterDTO, pageable));
    }


    @Operation(
            summary = "delete appointment",
            description = "Only OFFICE-MANAGER can delete appointment",
            responses = {
                    @ApiResponse(responseCode = "204", description = "appointment deleted"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<Void> deleteAppointment(
            @Parameter(description = "ID of the appointment to delete", required = true)
            @PathVariable Long id) {
        log.info("Request to delete appointment with ID {}", id);
        appointmentService.deleteAppointment(id);
        log.info("Successfully deleted appointment with ID {}", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update appointment",
            description = "Only OFFICE-MANAGER can perform this action",
            responses = {
                    @ApiResponse(responseCode = "200", description = "appointment updated"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(
            @Parameter(description = "ID of the appointment to update", required = true)
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated appointment details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AppointmentRequestDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody AppointmentRequestDTO dto) {
        log.info("Request to update appointment with ID {}", id);
        AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointment(id, dto);
        log.info("Successfully updated appointment with ID {}", id);

        return ResponseEntity.ok(updatedAppointment);
    }

    @Operation(
            summary = "Cancel appointment",
            description = "Only OFFICE-MANAGER or associate patient can perform this action. \n" +
                    "Only possible to cancel an appointment when it is in one of these states: REQUESTED, SCHEDULED, RESCHEDULE_REQUESTED, RESCHEDULED",

            responses = {
                    @ApiResponse(responseCode = "200", description = "appointment cancelled or cancellation requested."),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('OFFICE-MANAGER', 'PATIENT')")
    public ResponseEntity<?> cancelAppointment(
            @Parameter(description = "ID of the appointment to cancel", required = true)
            @PathVariable Long id) {
        log.info("Request to cancel appointment with ID {}", id);
        String message = appointmentService.cancelAppointment(id);
        log.info("Successfully cancelled appointment with ID {}", id);
        return ResponseEntity.ok(new MessageResponseDTO(message));
    }

    @Operation(
            summary = "Reschedule appointment",
            description = "Only OFFICE-MANAGER or associate patient can perform this action. \n" +
                    "Only possible to reschedule an appointment when it is in one of these states: REQUESTED, SCHEDULED, RESCHEDULE_REQUESTED, CANCELLATION_REQUESTED",

            responses = {
                    @ApiResponse(responseCode = "200", description = "appointment rescheduled or reschedule requested."),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('OFFICE-MANAGER', 'PATIENT')")
    public ResponseEntity<AppointmentResponseDTO> rescheduleAppointment(
            @Parameter(description = "ID of the appointment to reschedule", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New appointment date",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RescheduleAppointmentDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody RescheduleAppointmentDTO dto) {
        log.info("Request to reschedule appointment with ID {}", id);
        AppointmentResponseDTO updatedAppointment = appointmentService.rescheduleAppointment(id, dto);
        log.info("Successfully rescheduled appointment with ID {}", id);
        return ResponseEntity.ok(updatedAppointment);
    }

    // Generate bill
    @Operation(
            summary = "Generate bill",
            description = "Only OFFICE-MANAGER can perform this action.",

            responses = {
                    @ApiResponse(responseCode = "201", description = "Bill created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @PostMapping("/{id}/bills")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<BillResponseDTO> generateBill(
            @Parameter(description = "ID of the appointment", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Bill details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = BillRequestDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody BillRequestDTO dto) {
        log.info("Request to generate bill for appointment ID {}", id);
        BillResponseDTO response = appointmentService.generateBill(id, dto);
        log.info("Successfully generated bill for appointment ID {}", id);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Make a payment
    @Operation(
            summary = "Register a payment",
            description = "Only OFFICE-MANAGER can perform this action.",

            responses = {
                    @ApiResponse(responseCode = "201", description = "Bill created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Not authorized")
            }
    )
    @PostMapping("/{id}/payments")
    @PreAuthorize("hasRole('OFFICE-MANAGER')")
    public ResponseEntity<PaymentResponseDTO> registerPayment(
            @Parameter(description = "ID of the appointment", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payment details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PaymentRequestDTO.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody PaymentRequestDTO dto) {
        log.info("Request to register payment for appointment ID {}", id);
        PaymentResponseDTO response = appointmentService.makePayment(id, dto);
        log.info("Successfully registered payment for appointment ID {}", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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

