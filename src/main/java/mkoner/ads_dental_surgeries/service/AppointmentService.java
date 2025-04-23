package mkoner.ads_dental_surgeries.service;

import mkoner.ads_dental_surgeries.dto.appointment.AppointmentRequestDTO;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentResponseDTO;
import mkoner.ads_dental_surgeries.dto.appointment.RescheduleAppointmentDTO;
import mkoner.ads_dental_surgeries.dto.bill.BillRequestDTO;
import mkoner.ads_dental_surgeries.dto.bill.BillResponseDTO;
import mkoner.ads_dental_surgeries.dto.payment.PaymentRequestDTO;
import mkoner.ads_dental_surgeries.dto.payment.PaymentResponseDTO;
import mkoner.ads_dental_surgeries.model.Appointment;
import mkoner.ads_dental_surgeries.model.AppointmentStatus;

import java.util.List;

public interface AppointmentService {
    List<AppointmentResponseDTO> getAllAppointments();
    AppointmentResponseDTO getAppointmentById(Long id);
    AppointmentResponseDTO saveAppointment(AppointmentRequestDTO appointment);
    void deleteAppointment(Long id);
    List<AppointmentResponseDTO> getAppointmentsByPatient(Long patientId);
    List<AppointmentResponseDTO> getAppointmentsByDentist(Long dentistId);
    List<AppointmentResponseDTO> getAppointmentsByStatus(AppointmentStatus status);
    BillResponseDTO generateBill(Long appointmentId, BillRequestDTO billRequestDTO);
    PaymentResponseDTO makePayment(Long appointmentId, PaymentRequestDTO paymentRequestDTO);
    AppointmentResponseDTO updateAppointment(Long appointmentId, AppointmentRequestDTO appointmentRequestDTO);
    AppointmentResponseDTO rescheduleAppointment(Long appointmentId, RescheduleAppointmentDTO rescheduleAppointmentDTO);
    void cancelAppointment(Long appointmentId);
}

