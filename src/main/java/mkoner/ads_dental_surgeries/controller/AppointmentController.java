package mkoner.ads_dental_surgeries.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentRequestDTO;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentResponseDTO;
import mkoner.ads_dental_surgeries.dto.bill.BillRequestDTO;
import mkoner.ads_dental_surgeries.dto.bill.BillResponseDTO;
import mkoner.ads_dental_surgeries.dto.payment.PaymentRequestDTO;
import mkoner.ads_dental_surgeries.dto.payment.PaymentResponseDTO;
import mkoner.ads_dental_surgeries.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@Valid @RequestBody AppointmentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.saveAppointment(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(@PathVariable Long id,
                                                            @Valid @RequestBody AppointmentRequestDTO dto) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, dto));
    }

    // Generate bill
    @PostMapping("/{id}/bills")
    public ResponseEntity<BillResponseDTO> generateBill(@PathVariable Long id, @Valid @RequestBody BillRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.generateBill(id, dto));
    }

    //Make a payment
    @PostMapping("/{id}/payments")
    public ResponseEntity<PaymentResponseDTO> makePayment(@PathVariable Long id,@Valid @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.makePayment(id, dto));
    }
}

