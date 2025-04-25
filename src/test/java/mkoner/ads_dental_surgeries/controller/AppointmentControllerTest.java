package mkoner.ads_dental_surgeries.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import mkoner.ads_dental_surgeries.config.JwtUtil;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentRequestDTO;
import mkoner.ads_dental_surgeries.dto.appointment.AppointmentResponseDTO;
import mkoner.ads_dental_surgeries.dto.dentist.DentistResponseDTO;
import mkoner.ads_dental_surgeries.dto.patient.PatientResponseDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryRequestDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryResponseDTO;
import mkoner.ads_dental_surgeries.model.*;
import mkoner.ads_dental_surgeries.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;


    @MockitoBean
    private AppointmentService appointmentService;

    private AppointmentRequestDTO requestDTO;
    private AppointmentResponseDTO responseDTO;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        DentistResponseDTO dentist = mock(DentistResponseDTO.class);
        PatientResponseDTO patient = mock(PatientResponseDTO.class);
        SurgeryResponseDTO surgery = mock(SurgeryResponseDTO.class);
        requestDTO = new AppointmentRequestDTO(now, 1L, 1L, 1L, AppointmentStatus.SCHEDULED);
        responseDTO = new AppointmentResponseDTO(1L, now, patient, dentist, surgery, AppointmentStatus.SCHEDULED, null);
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should create a new appointment and return 201 Created")
    void shouldCreateAppointment() throws Exception {

        when(appointmentService.saveAppointment(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentId").value(responseDTO.appointmentId()));
    }


    @Test
    @DisplayName("Should delete an appointment and return 204 No Content")
    void shouldDeleteAppointment() throws Exception {
        Long id = 1L;

        doNothing().when(appointmentService).deleteAppointment(id);

        mockMvc.perform(delete("/api/appointments/{id}", id))
                .andExpect(status().isNoContent());
    }

}
