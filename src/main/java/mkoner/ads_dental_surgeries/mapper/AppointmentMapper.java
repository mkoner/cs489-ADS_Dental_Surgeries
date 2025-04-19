package mkoner.ads_dental_surgeries.mapper;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentResponseDTO;
import mkoner.ads_dental_surgeries.model.Appointment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AppointmentMapper {
    private final DentistMapper dentistMapper;
    private final PatientMapper patientMapper;
    private final SurgeryMapper surgeryMapper;;
    private final  BillMapper billMapper;
    public Appointment mapToAppointment(AppointmentResponseDTO appointmentResponseDTO) {
        return new Appointment();
    }
    public AppointmentResponseDTO mapToAppointmentResponseDTO(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getAppointmentId(),
                appointment.getDateTime(),
                patientMapper.mapToPatientResponseDTO(appointment.getPatient()),
                dentistMapper.mapToDentistResponseDTO(appointment.getDentist()),
                surgeryMapper.mapToSurgeryResponseDTO(appointment.getSurgery()),
                appointment.getStatus(),
                billMapper.mapToBillResponseDTO(appointment.getBill())

        );
    }
}
