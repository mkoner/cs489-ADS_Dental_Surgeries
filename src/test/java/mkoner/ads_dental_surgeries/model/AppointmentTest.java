package mkoner.ads_dental_surgeries.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;

import mkoner.ads_dental_surgeries.exception.domain_exception.InvalidCancellationStatus;
import mkoner.ads_dental_surgeries.exception.domain_exception.InvalidRescheduleStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AppointmentTest {

    private Appointment appointment;
    private Patient patient;
    private Dentist dentist;
    private Surgery surgery;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patient = mock(Patient.class);
        dentist = mock(Dentist.class);
        surgery = mock(Surgery.class);

        appointment = new Appointment(LocalDateTime.now(), AppointmentStatus.REQUESTED, patient, dentist, surgery);
    }

    @Test
    void testCancel_ValidStatus() {
        appointment.cancel(AppointmentStatus.CANCELLATION_REQUESTED);
        assertEquals(AppointmentStatus.CANCELLATION_REQUESTED, appointment.getStatus());
    }

    @Test
    void testCancel_InvalidStatus_ThrowsException() {
        appointment = new Appointment(LocalDateTime.now(), AppointmentStatus.COMPLETED, patient, dentist, surgery);

        assertThrows(InvalidCancellationStatus.class, () -> appointment.cancel(AppointmentStatus.CANCELLATION_REQUESTED));
    }

    @Test
    void testReschedule_ValidStatus() {
        LocalDateTime newDateTime = LocalDateTime.now().plusDays(2);
        appointment.reschedule(newDateTime, AppointmentStatus.RESCHEDULE_REQUESTED);

        assertEquals(newDateTime, appointment.getDateTime());
        assertEquals(AppointmentStatus.RESCHEDULE_REQUESTED, appointment.getStatus());
    }

    @Test
    void testReschedule_InvalidStatus_ThrowsException() {
        appointment = new Appointment(LocalDateTime.now(), AppointmentStatus.COMPLETED, patient, dentist, surgery);

        assertThrows(InvalidRescheduleStatus.class, () -> appointment.reschedule(LocalDateTime.now().plusDays(2), AppointmentStatus.RESCHEDULE_REQUESTED));
    }

    @Test
    void testGenerateBill() {
        Money amount = mock(Money.class);
        LocalDate dueDate = LocalDate.now().plusDays(30);

        appointment.generateBill(amount, dueDate);

        assertNotNull(appointment.getBill());
        assertEquals(dueDate, appointment.getBill().getDueDate());
    }
}