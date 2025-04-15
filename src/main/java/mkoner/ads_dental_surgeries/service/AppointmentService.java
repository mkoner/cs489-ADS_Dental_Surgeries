package mkoner.ads_dental_surgeries.service;

import mkoner.ads_dental_surgeries.model.Appointment;
import mkoner.ads_dental_surgeries.model.AppointmentStatus;

import java.util.List;

public interface AppointmentService {
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Long id);
    Appointment saveAppointment(Appointment appointment);
    void deleteAppointment(Long id);
    List<Appointment> getAppointmentsByPatient(Long patientId);
    List<Appointment> getAppointmentsByDentist(Long dentistId);
    List<Appointment> getAppointmentsByStatus(AppointmentStatus status);
}

