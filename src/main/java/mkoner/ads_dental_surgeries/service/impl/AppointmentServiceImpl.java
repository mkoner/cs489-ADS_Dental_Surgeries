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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
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

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    public List<AppointmentResponseDTO> getAllAppointments() {
        logger.debug("Fetching all appointments");
        List<AppointmentResponseDTO> appointments = appointmentRepository.findAll().stream()
                .map(appointmentMapper::mapToAppointmentResponseDTO)
                .toList();
        logger.info("Fetched {} total appointments", appointments.size());
        return appointments;
    }

    public AppointmentResponseDTO getAppointmentById(Long id) {
        logger.debug("Fetching appointment with ID {}", id);
        var appointment = appointmentRepository.findById(id).orElseThrow(() -> {
            logger.warn("Appointment not found with ID {}", id);
            return new ResourceNotFoundException("Appointment with id " + id + " not found");
        });
        logger.info("Fetched appointment with ID {}", id);
        return appointmentMapper.mapToAppointmentResponseDTO(appointment);
    }

    public AppointmentResponseDTO saveAppointment(AppointmentRequestDTO appointmentRequestDTO) {
        Long patientId = appointmentRequestDTO.patientId();
        Long dentistId = appointmentRequestDTO.dentistId();
        Long surgeryId = appointmentRequestDTO.surgeryId();

        logger.debug("Received appointment request - Patient ID: {}, Dentist ID: {}, Surgery ID: {}",
                patientId, dentistId, surgeryId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(()->{
                    logger.warn("Patient not found with id {}", patientId);
                    return new ResourceNotFoundException("Patient with id " + patientId + " not found");
                });
        //Check if patient has any overdue unpaid bill
        List<Long> unpaidAppointments = getAppointmentIdsWithOverdueUnpaidBillsByPatientId(patientId);
        if(!unpaidAppointments.isEmpty()) {
            logger.info("Patient {} has unpaid appointments: {}", patientId, unpaidAppointments);
            throw new BadRequestException("Patient has overdue unpaid bills for these appointments: " + unpaidAppointments);
        }
        Dentist dentist = getDentistAndCheckIfExceedsAppointmentLimits(dentistId, LocalDate.from(appointmentRequestDTO.dateTime()));

        Surgery surgery = surgeryRepository.findById(surgeryId).
                orElseThrow(() -> {
                    logger.warn("Surgery not found with id {}", surgeryId);
                    return new ResourceNotFoundException("Surgery with id " + surgeryId + " not found");
                });
        AppointmentStatus status = isOfficeManager() ? AppointmentStatus.SCHEDULED : AppointmentStatus.REQUESTED;
        Appointment appointment = new Appointment(
                appointmentRequestDTO.dateTime(),
                status,
                patient,
                dentist,
                surgery
        );
        Appointment savedAppointment = appointmentRepository.save(appointment);
        logger.info("Appointment saved successfully with ID {}", savedAppointment.getAppointmentId());

        return appointmentMapper.mapToAppointmentResponseDTO(savedAppointment);
    }

    public void deleteAppointment(Long id) {
        logger.info("Attempting to delete appointment with ID {}", id);
        try {
            appointmentRepository.deleteById(id);
            logger.info("Successfully deleted appointment with ID {}", id);
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("Attempted to delete non-existing appointment with ID {}", id);
        }
    }

    public List<AppointmentResponseDTO> getAppointmentsByPatient(Long patientId) {
        logger.debug("Fetching appointments for patient ID {}", patientId);
        List<AppointmentResponseDTO> appointments = appointmentRepository.findByPatientUserId(patientId).stream()
                .map(appointmentMapper::mapToAppointmentResponseDTO)
                .toList();
        logger.info("Found {} appointments for patient ID {}", appointments.size(), patientId);
        return appointments;
    }

    public List<AppointmentResponseDTO> getAppointmentsByDentist(Long dentistId) {
        logger.debug("Fetching appointments for dentist ID {}", dentistId);
        List<AppointmentResponseDTO> appointments = appointmentRepository.findByDentistUserId(dentistId).stream()
                .map(appointmentMapper::mapToAppointmentResponseDTO)
                .toList();
        logger.info("Found {} appointments for dentist ID {}", appointments.size(), dentistId);
        return appointments;
    }

    public List<AppointmentResponseDTO> getAppointmentsByStatus(AppointmentStatus status) {
        logger.debug("Fetching appointments with status {}", status);
        List<AppointmentResponseDTO> appointments = appointmentRepository.findByStatus(status).stream()
                .map(appointmentMapper::mapToAppointmentResponseDTO)
                .toList();
        logger.info("Found {} appointments with status {}", appointments.size(), status);
        return appointments;
    }

    @Override
    @Transactional
    public BillResponseDTO generateBill(Long appointmentId, BillRequestDTO billRequestDTO) {
        logger.debug("Generating bill for appointment with ID {}", appointmentId);
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    logger.warn("Appointment not found with id {}", appointmentId);
                    return new ResourceNotFoundException("Appointment with id " + appointmentId + " not found");
                });
        Money money = new Money(billRequestDTO.amount(), billRequestDTO.currency(), billRequestDTO.currencySymbol());
        appointment.generateBill(money, billRequestDTO.dueDate());
        appointment = appointmentRepository.save(appointment);
        BillResponseDTO billResponseDTO = billMapper.mapToBillResponseDTO(appointment.getBill());
        logger.info("Generated bill for appointment with ID {}", appointmentId);
        return billResponseDTO;
    }

    @Override
    @Transactional
    public PaymentResponseDTO makePayment(Long appointmentId, PaymentRequestDTO paymentRequestDTO) {
        logger.debug("Making payment for appointment with ID {}", appointmentId);
        Appointment appointment = appointmentRepository.findById(appointmentId).
                orElseThrow(() -> {
                    logger.warn("Appointment not found with id {}", appointmentId);
                    return new ResourceNotFoundException("Appointment with id " + appointmentId + " not found");
                });
        Bill bill = appointment.getBill();
        if (bill == null) {
            logger.warn("Bill not found for appointment with ID {}", appointmentId);
            throw new BadRequestException("Appointment with id " + appointmentId + " has no associated bill");
        }
        Money money = new Money(paymentRequestDTO.amount(), bill.getAmount().getCurrency(), bill.getAmount().getCurrencySymbol());
        bill.makePayment(money);
        appointmentRepository.save(appointment);
        Payment payment = appointment.getBill().getPayments().getLast();
        logger.info("Registered payment of {} for appointment with ID {}", payment.getAmount(), appointmentId);
        return paymentMapper.mapToPaymentResponseDTO(payment);
    }

    @Override
    public AppointmentResponseDTO updateAppointment(Long appointmentId, AppointmentRequestDTO appointmentRequestDTO) {
        logger.debug("Updating appointment with ID {}", appointmentId);
        var existingAppointment = appointmentRepository.findById(appointmentId).orElseThrow(()->{
            logger.warn("Appointment not found with id {}", appointmentId);
            return new ResourceNotFoundException("Appointment with id " + appointmentId + " not found");
        });
        Long patientId = appointmentRequestDTO.patientId();
        Long dentistId = appointmentRequestDTO.dentistId();
        Long surgeryId = appointmentRequestDTO.surgeryId();
        if(!existingAppointment.getPatient().getUserId().equals(patientId)){
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(()->{
                        logger.warn("Patient not found with id {}", patientId);
                        return new ResourceNotFoundException("Patient with id " + patientId + " not found");
                    });
            existingAppointment.setPatient(patient);
        }
        if(!existingAppointment.getSurgery().getSurgeryId().equals(surgeryId)){
            Surgery surgery =  surgeryRepository.findById(surgeryId).
                    orElseThrow(() -> {
                        logger.warn("Surgery not found with id {}", surgeryId);
                        return new ResourceNotFoundException("Surgery with id " + surgeryId + " not found");
                    });
            existingAppointment.setSurgery(surgery);
        }
        Dentist dentist = getDentistAndCheckIfExceedsAppointmentLimits(dentistId, LocalDate.from(appointmentRequestDTO.dateTime()));
        existingAppointment.setDentist(dentist);
        existingAppointment.setDateTime(appointmentRequestDTO.dateTime());
        existingAppointment.setStatus(appointmentRequestDTO.status());
        Appointment appointment = appointmentRepository.save(existingAppointment);
        logger.info("Updated payment for appointment with ID {}", appointmentId);
        return appointmentMapper.mapToAppointmentResponseDTO(appointment);
    }
    @Override
    public AppointmentResponseDTO rescheduleAppointment(Long id, RescheduleAppointmentDTO dto) {
        logger.debug("Rescheduling appointment with ID {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Appointment not found with id {}", id);
                    return new ResourceNotFoundException("Appointment with id " + id + " not found");
                });
        if(isLoggedUserNotAuthorizedToPerformAction(appointment.getPatient().getEmailAddress())){
            logger.warn("User {} is not authorized to reschedule appointment {}", appointment.getPatient().getEmailAddress(), id);
            throw new AccessDeniedException("You are not authorized to reschedule this appointment.");
        }

        if (isOfficeManager()) {
            //Check if dentist has more than 5 appointments
            if(appointment.getDentist() != null && dentistExceedsAppointmentLimit(appointment.getDentist().getUserId(), LocalDate.from(dto.newDateTime()))){
                logger.info("Dentist {} reached appointment limit", appointment.getDentist().getUserId());
                throw new BadRequestException("Dentist already has 5 appointments this week.");
            }
            appointment.reschedule(dto.newDateTime(), AppointmentStatus.RESCHEDULED);
        } else {
            appointment.reschedule(dto.newDateTime(), AppointmentStatus.RESCHEDULE_REQUESTED);
        }
        Appointment updated = appointmentRepository.save(appointment);
        logger.info("Appointment rescheduled: {}", id);
        return appointmentMapper.mapToAppointmentResponseDTO(updated);
    }
    @Override
    public String cancelAppointment(Long id) {
        logger.debug("Cancelling appointment with ID {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Appointment not found with id {}", id);
                    return new ResourceNotFoundException("Appointment with id " + id + " not found");
                });
        if(isLoggedUserNotAuthorizedToPerformAction(appointment.getPatient().getEmailAddress())){
            logger.warn("User {} is not authorized to cancel appointment {}", appointment.getPatient().getEmailAddress(), id);
            throw new AccessDeniedException("You are not authorized to cancel this appointment.");
        }
        String message;
        if (isOfficeManager()) {
            appointment.cancel(AppointmentStatus.CANCELLED);
            message = "Appointment: " + id + " has been cancelled";
        } else {
            appointment.cancel(AppointmentStatus.CANCELLATION_REQUESTED);
            message = "Cancellation request for Appointment: " + id + " succeeded";
        }
        appointmentRepository.save(appointment);
        logger.info("Appointment cancelled: {}", id);
        return message;
    }

    public Page<AppointmentResponseDTO> getFilteredAppointments(AppointmentFilterDTO filterDTO, Pageable pageable) {
        logger.debug("Filtering appointments with criteria: {}", filterDTO);
        Specification<Appointment> spec = Specification.where(null);
        if (filterDTO.appointmentDate() != null) spec = spec.and(AppointmentSpecification.hasAppointmentDate(filterDTO.appointmentDate()));
        if (filterDTO.status() != null) spec = spec.and(AppointmentSpecification.hasStatus(filterDTO.status()));
        if (filterDTO.patientEmail() != null) spec = spec.and(AppointmentSpecification.hasPatientEmail(filterDTO.patientEmail()));
        if (filterDTO.dentistEmail() != null) spec = spec.and(AppointmentSpecification.hasDentistEmail(filterDTO.dentistEmail()));
        if (filterDTO.surgeryCountry() != null) spec = spec.and(AppointmentSpecification.hasSurgeryCountry(filterDTO.surgeryCountry()));
        if (filterDTO.surgeryCity() != null) spec = spec.and(AppointmentSpecification.hasSurgeryCity(filterDTO.surgeryCity()));
        if (filterDTO.paymentStatus() != null) spec = spec.and(AppointmentSpecification.hasPaymentStatus(filterDTO.paymentStatus()));

        var appointments = appointmentRepository.findAll(spec, pageable);
        logger.info("Filtered appointments fetched: {} records on page {}", appointments.getNumberOfElements(), pageable.getPageNumber());
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
    private Dentist getDentistAndCheckIfExceedsAppointmentLimits(Long dentistId, LocalDate date) {
        if(dentistId == null) return null;

        Dentist dentist = dentistRepository.findById(dentistId)
                    .orElseThrow(() -> {
                        logger.warn("Dentist not found with id {}", dentistId);
                        return new ResourceNotFoundException("Dentist with id " + dentistId + " not found");
                    });

            if (dentistExceedsAppointmentLimit(dentistId, date)) {
                logger.info("Dentist with id {} has reached weekly appointment limit", dentistId);
                throw new BadRequestException("Dentist already has 5 appointments this week.");
            }
            return dentist;
    }

    private boolean isLoggedUserNotAuthorizedToPerformAction(String patientEmail) {
        if(isOfficeManager()){
            return false;
        }
        String loggedInUserEmail;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            loggedInUserEmail = userDetails.getUsername(); // usually the email
        }
        else {
            loggedInUserEmail = principal.toString();
        }
        return !loggedInUserEmail.equals(patientEmail);
    }
}

