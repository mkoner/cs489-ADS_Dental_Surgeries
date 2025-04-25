package mkoner.ads_dental_surgeries.service.impl;

import mkoner.ads_dental_surgeries.dto.appointment.AppointmentRequestDTO;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentResponseDTO;
import mkoner.ads_dental_surgeries.exception.custom_exception.ResourceNotFoundException;
import mkoner.ads_dental_surgeries.mapper.AppointmentMapper;
import mkoner.ads_dental_surgeries.mapper.BillMapper;
import mkoner.ads_dental_surgeries.mapper.PaymentMapper;
import mkoner.ads_dental_surgeries.model.*;
import mkoner.ads_dental_surgeries.repository.AppointmentRepository;
import mkoner.ads_dental_surgeries.repository.DentistRepository;
import mkoner.ads_dental_surgeries.repository.PatientRepository;
import mkoner.ads_dental_surgeries.repository.SurgeryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DentistRepository dentistRepository;
    @Mock
    private SurgeryRepository surgeryRepository;
    @Mock
    private BillMapper billMapper;
    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    @DisplayName("Should return list of all appointments")
    void testGetAllAppointments() {
        Appointment appointment = new Appointment();
        AppointmentResponseDTO dto = mock(AppointmentResponseDTO.class);

        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));
        when(appointmentMapper.mapToAppointmentResponseDTO(appointment)).thenReturn(dto);

        List<AppointmentResponseDTO> result = appointmentService.getAllAppointments();

        assertThat(result).hasSize(1).contains(dto);
        verify(appointmentRepository).findAll();
        verify(appointmentMapper).mapToAppointmentResponseDTO(appointment);
    }

    @Test
    @DisplayName("Should return appointment by ID")
    void testGetAppointmentById() {
        long id = 1L;
        Appointment appointment = new Appointment();
        AppointmentResponseDTO dto = mock(AppointmentResponseDTO.class);

        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.mapToAppointmentResponseDTO(appointment)).thenReturn(dto);

        AppointmentResponseDTO result = appointmentService.getAppointmentById(id);

        assertThat(result).isEqualTo(dto);
        verify(appointmentRepository).findById(id);
        verify(appointmentMapper).mapToAppointmentResponseDTO(appointment);
    }

    @Test
    @DisplayName("Should throw when appointment not found by ID")
    void testGetAppointmentByIdNotFound() {
        long id = 1L;

        when(appointmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.getAppointmentById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Appointment with id " + id + " not found");
    }

    @Test
    @DisplayName("Should delete appointment by ID")
    void testDeleteAppointment() {
        long id = 1L;

        appointmentService.deleteAppointment(id);

        verify(appointmentRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should save appointment successfully when patient, dentist, and surgery exist and no unpaid bills")
    void testSaveAppointmentSuccess() {

        Long patientId = 1L;
        Long dentistId = 2L;
        Long surgeryId = 3L;
        LocalDateTime dateTime = LocalDateTime.now();

        AppointmentRequestDTO requestDTO = new AppointmentRequestDTO(dateTime, patientId, dentistId, surgeryId, AppointmentStatus.REQUESTED);
        Patient patient = mock(Patient.class);
        Dentist dentist = mock(Dentist.class);
        Surgery surgery = mock(Surgery.class);
        Appointment savedAppointment = mock(Appointment.class);
        AppointmentResponseDTO responseDTO = mock(AppointmentResponseDTO.class);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(dentistId)).thenReturn(Optional.of(dentist));
        when(surgeryRepository.findById(surgeryId)).thenReturn(Optional.of(surgery));
        when(appointmentRepository.findByPatientUserId(patientId)).thenReturn(List.of());
        when(appointmentRepository.countAppointmentsForDentistInWeek(eq(dentistId), any(), any())).thenReturn(0L);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);
        when(appointmentMapper.mapToAppointmentResponseDTO(savedAppointment)).thenReturn(responseDTO);


        AppointmentResponseDTO result = appointmentService.saveAppointment(requestDTO);


        assertThat(result).isEqualTo(responseDTO);
        verify(appointmentRepository).save(any(Appointment.class));
    }
}
