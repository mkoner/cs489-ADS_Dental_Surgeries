package mkoner.ads_dental_surgeries.dto.appointment;

import mkoner.ads_dental_surgeries.dto.bill.BillResponseDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistResponseDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientResponseDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryResponseDTO;
import mkoner.ads_dental_surgeries.model.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponseDTO(
        Long appointmentId,
        LocalDateTime dateTime,
        PatientResponseDTO patient,
        DentistResponseDTO dentist,
        SurgeryResponseDTO surgery,
        AppointmentStatus status,
        BillResponseDTO bill
) {
}
