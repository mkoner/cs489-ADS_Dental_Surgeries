package mkoner.ads_dental_surgeries.service.impl;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentFilterDTO;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentRequestDTO;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentResponseDTO;
import mkoner.ads_dental_surgeries.dto.appointment.RescheduleAppointmentDTO;
import mkoner.ads_dental_surgeries.dto.bill.BillRequestDTO;
import mkoner.ads_dental_surgeries.dto.bill.BillResponseDTO;
import mkoner.ads_dental_surgeries.dto.payment.PaymentRequestDTO;
import mkoner.ads_dental_surgeries.dto.payment.PaymentResponseDTO;
import mkoner.ads_dental_surgeries.exception.custom_exception.BadRequestException;
import mkoner.ads_dental_surgeries.exception.custom_exception.ResourceNotFoundException;
import mkoner.ads_dental_surgeries.filter_specification.AppointmentSpecification;
import mkoner.ads_dental_surgeries.mapper.AppointmentMapper;
import mkoner.ads_dental_surgeries.mapper.BillMapper;
import mkoner.ads_dental_surgeries.mapper.PaymentMapper;
import mkoner.ads_dental_surgeries.model.*;
import mkoner.ads_dental_surgeries.repository.AppointmentRepository;
import mkoner.ads_dental_surgeries.repository.DentistRepository;
import mkoner.ads_dental_surgeries.repository.PatientRepository;
import mkoner.ads_dental_surgeries.repository.SurgeryRepository;
import mkoner.ads_dental_surgeries.service.AppointmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final SurgeryRepository surgeryRepository;
    private final BillMapper billMapper;
    private final PaymentMapper paymentMapper;

    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(appointmentMapper::mapToAppointmentResponseDTO).toList();
    }

    public AppointmentResponseDTO getAppointmentById(Long id) {
        var appointment = appointmentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Appointment with id " + id + " not found"));
        return appointmentMapper.mapToAppointmentResponseDTO(appointment);
    }

    public AppointmentResponseDTO saveAppointment(AppointmentRequestDTO appointmentRequestDTO) {
        Long patientId = appointmentRequestDTO.patientId();
        Long dentistId = appointmentRequestDTO.dentistId();
        Long surgeryId = appointmentRequestDTO.surgeryId();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(()->new ResourceNotFoundException("Patient with id " + patientId + " not found"));
        //Check if patient has any overdue unpaid bill
        List<Long> unpaidAppointments = getAppointmentIdsWithOverdueUnpaidBillsByPatientId(patientId);
        if(!unpaidAppointments.isEmpty()) {
            throw new BadRequestException("Patient with id " + patientId + " has overdue unpaid bills for these appointments: " + unpaidAppointments);
        }
        Dentist dentist = dentistId == null ? null : dentistRepository.findById(dentistId)
                        .orElseThrow(() -> new ResourceNotFoundException("Dentist with id " + dentistId + " not found"));
        //Check if dentist has more than 5 appointments
        if(dentistExceedsAppointmentLimit(dentistId, LocalDate.from(appointmentRequestDTO.dateTime()))){
            throw new BadRequestException("Dentist already has 5 appointments this week.");
        }
        Surgery surgery = surgeryRepository.findById(surgeryId).
                orElseThrow(() -> new ResourceNotFoundException("Surgery with id " + surgeryId + " not found"));
        AppointmentStatus status;
        if(isOfficeManager()){
            status = AppointmentStatus.SCHEDULED;
        }
        else{
            status = AppointmentStatus.REQUESTED;
        }
        Appointment appointment = new Appointment(
                appointmentRequestDTO.dateTime(),
                status,
                patient,
                dentist,
                surgery
        );
        return appointmentMapper.mapToAppointmentResponseDTO(appointmentRepository.save(appointment));
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public List<AppointmentResponseDTO> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientUserId(patientId).stream()
                .map(appointmentMapper::mapToAppointmentResponseDTO).toList();
    }

    public List<AppointmentResponseDTO> getAppointmentsByDentist(Long dentistId) {
        return appointmentRepository.findByDentistUserId(dentistId).stream()
                .map(appointmentMapper::mapToAppointmentResponseDTO).toList();
    }

    public List<AppointmentResponseDTO> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(appointmentMapper::mapToAppointmentResponseDTO).toList();
    }

    @Override
    @Transactional
    public BillResponseDTO generateBill(Long appointmentId, BillRequestDTO billRequestDTO) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment with id " + appointmentId + " not found"));
        Money money = new Money(billRequestDTO.amount(), billRequestDTO.currency(), billRequestDTO.currencySymbol());
        appointment.generateBill(money, billRequestDTO.dueDate());
        appointment = appointmentRepository.save(appointment);
        return billMapper.mapToBillResponseDTO(appointment.getBill());
    }

    @Override
    @Transactional
    public PaymentResponseDTO makePayment(Long appointmentId, PaymentRequestDTO paymentRequestDTO) {
        Appointment appointment = appointmentRepository.findById(appointmentId).
                orElseThrow(() -> new ResourceNotFoundException("Appointment with id " + appointmentId + " not found"));
        Bill bill = appointment.getBill();
        if (bill == null) {
            throw new BadRequestException("Appointment with id " + appointmentId + " has no associated bill");
        }
        Money money = new Money(paymentRequestDTO.amount(), bill.getAmount().getCurrency(), bill.getAmount().getCurrencySymbol());
        bill.makePayment(money);
        appointmentRepository.save(appointment);
        Payment payment = appointment.getBill().getPayments().get(appointment.getBill().getPayments().size() - 1);
        return paymentMapper.mapToPaymentResponseDTO(payment);
    }

    @Override
    public AppointmentResponseDTO updateAppointment(Long appointmentId, AppointmentRequestDTO appointmentRequestDTO) {
        var existingAppointment = appointmentRepository.findById(appointmentId).orElseThrow(()->new ResourceNotFoundException("Appointment with id " + appointmentId + " not found"));
        Long patientId = appointmentRequestDTO.patientId();
        Long dentistId = appointmentRequestDTO.dentistId();
        Long surgeryId = appointmentRequestDTO.surgeryId();
        //Check if dentist has more than 5 appointments
        if(dentistExceedsAppointmentLimit(dentistId, LocalDate.from(appointmentRequestDTO.dateTime()))){
            throw new BadRequestException("Dentist already has 5 appointments this week.");
        }
        if(!existingAppointment.getPatient().getUserId().equals(patientId)){
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(()->new ResourceNotFoundException("Patient with id " + patientId + " not found"));
            existingAppointment.setPatient(patient);
        }
        if(!existingAppointment.getSurgery().getSurgeryId().equals(surgeryId)){
            Surgery surgery =  surgeryRepository.findById(surgeryId).
                    orElseThrow(() -> new ResourceNotFoundException("Surgery with id " + surgeryId + " not found"));
            existingAppointment.setSurgery(surgery);
        }
        Dentist dentist = dentistId != null ?
                dentistRepository.findById(dentistId).
                orElseThrow(() -> new ResourceNotFoundException("Dentist with id " + dentistId + " not found"))
                : null;
        existingAppointment.setDentist(dentist);
        existingAppointment.setDateTime(appointmentRequestDTO.dateTime());
        existingAppointment.setStatus(appointmentRequestDTO.status());
        return appointmentMapper.mapToAppointmentResponseDTO(appointmentRepository.save(existingAppointment));
    }
    @Override
    public AppointmentResponseDTO rescheduleAppointment(Long id, RescheduleAppointmentDTO dto) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if(!isLoggedUserAuthorizedToPerformAction(appointment.getPatient().getEmailAddress())){
            throw new AccessDeniedException("You are not authorized to reschedule this appointment.");
        }

        if (isOfficeManager()) {
            //Check if dentist has more than 5 appointments
            if(appointment.getDentist() != null && dentistExceedsAppointmentLimit(appointment.getDentist().getUserId(), LocalDate.from(dto.newDateTime()))){
                throw new BadRequestException("Dentist already has 5 appointments this week.");
            }
            appointment.reschedule(dto.newDateTime(), AppointmentStatus.RESCHEDULED);
        } else {
            appointment.reschedule(dto.newDateTime(), AppointmentStatus.RESCHEDULE_REQUESTED);
        }
        Appointment updated = appointmentRepository.save(appointment);

        return appointmentMapper.mapToAppointmentResponseDTO(updated);
    }
    @Override
    public String cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if(!isLoggedUserAuthorizedToPerformAction(appointment.getPatient().getEmailAddress())){
            throw new AccessDeniedException("You are not authorized to cancel this appointment.");
        }
        String message = "";
        if (isOfficeManager()) {
            appointment.cancel(AppointmentStatus.CANCELLED);
            message = "Appointment: " + id + " has been cancelled";
        } else {
            appointment.cancel(AppointmentStatus.CANCELLATION_REQUESTED);
            message = "Cancellation request for Appointment: " + id + " succeeded";
        }

        appointmentRepository.save(appointment);
        return message;
    }

    public Page<AppointmentResponseDTO> getFilteredAppointments(AppointmentFilterDTO filterDTO, Pageable pageable) {
        Specification<Appointment> spec = Specification.where(null);

        if (filterDTO.appointmentDate() != null) spec = spec.and(AppointmentSpecification.hasAppointmentDate(filterDTO.appointmentDate()));
        if (filterDTO.status() != null) spec = spec.and(AppointmentSpecification.hasStatus(filterDTO.status()));
        if (filterDTO.patientEmail() != null) spec = spec.and(AppointmentSpecification.hasPatientEmail(filterDTO.patientEmail()));
        if (filterDTO.dentistEmail() != null) spec = spec.and(AppointmentSpecification.hasDentistEmail(filterDTO.dentistEmail()));
        if (filterDTO.surgeryCountry() != null) spec = spec.and(AppointmentSpecification.hasSurgeryCountry(filterDTO.surgeryCountry()));
        if (filterDTO.surgeryCity() != null) spec = spec.and(AppointmentSpecification.hasSurgeryCity(filterDTO.surgeryCity()));
        if (filterDTO.paymentStatus() != null) spec = spec.and(AppointmentSpecification.hasPaymentStatus(filterDTO.paymentStatus()));

        var appointments = appointmentRepository.findAll(spec, pageable);
        return appointments.map(appointmentMapper::mapToAppointmentResponseDTO);
    }


    private List<Long> getAppointmentIdsWithOverdueUnpaidBillsByPatientId(Long patientId) {
        LocalDate today = LocalDate.now();
        return appointmentRepository.findByPatientUserId(patientId).stream()
                .filter(appointment -> {
                    Bill bill = appointment.getBill();
                    return bill != null &&
                            bill.getPaymentStatus() != PaymentStatus.PAID &&
                            bill.getDueDate() != null &&
                            bill.getDueDate().isBefore(today);
                })
                .map(Appointment::getAppointmentId)
                .collect(Collectors.toList());
    }

    private boolean isOfficeManager() {
        return SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + "OFFICE-MANAGER"));
    }
    private LocalDate getStartOfWeek(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

    private LocalDate getEndOfWeek(LocalDate date) {
        return date.with(DayOfWeek.SUNDAY);
    }
    private boolean dentistExceedsAppointmentLimit(Long dentistId, LocalDate date) {
        LocalDate startOfWeek = getStartOfWeek(date);
        LocalDate endOfWeek = getEndOfWeek(date);

        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endDateTime = endOfWeek.atTime(LocalTime.MAX);

        long existingAppointments = appointmentRepository
                .countAppointmentsForDentistInWeek(dentistId, startDateTime, endDateTime);

        return existingAppointments >= 5;
    }

    private boolean isLoggedUserAuthorizedToPerformAction(String patientEmail) {
        if(isOfficeManager()){
            return true;
        }
        String loggedInUserEmail;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            loggedInUserEmail = userDetails.getUsername(); // usually the email
        }
        else {
            loggedInUserEmail = principal.toString();
        }
        return loggedInUserEmail.equals(patientEmail);
    }
}

