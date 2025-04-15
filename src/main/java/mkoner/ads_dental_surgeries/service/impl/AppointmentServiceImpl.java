package mkoner.ads_dental_surgeries.service.impl;

import mkoner.ads_dental_surgeries.model.Appointment;
import mkoner.ads_dental_surgeries.model.AppointmentStatus;
import mkoner.ads_dental_surgeries.repository.AppointmentRepository;
import mkoner.ads_dental_surgeries.service.AppointmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientUserId(patientId);
    }

    public List<Appointment> getAppointmentsByDentist(Long dentistId) {
        return appointmentRepository.findByDentistUserId(dentistId);
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }
}

