package mkoner.ads_dental_surgeries.filter_specification;

import mkoner.ads_dental_surgeries.model.Appointment;
import mkoner.ads_dental_surgeries.model.AppointmentStatus;
import mkoner.ads_dental_surgeries.model.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;

import java.beans.Expression;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AppointmentSpecification {

    public static Specification<Appointment> hasAppointmentDate(LocalDate date) {
        return (root, query, cb) -> {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            return cb.between(root.get("dateTime"), startOfDay, endOfDay);
        };
    }


    public static Specification<Appointment> hasStatus(AppointmentStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Appointment> hasPatientEmail(String email) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("patient").get("emailAddress")), email.toLowerCase());
    }

    public static Specification<Appointment> hasDentistEmail(String email) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("dentist").get("emailAddress")), email.toLowerCase());
    }

    public static Specification<Appointment> hasSurgeryCountry(String country) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("surgery").get("address").get("country")), country.toLowerCase());
    }

    public static Specification<Appointment> hasSurgeryCity(String city) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("surgery").get("address").get("city")), city.toLowerCase());
    }

    public static Specification<Appointment> hasPaymentStatus(PaymentStatus status) {
        return (root, query, cb) -> cb.equal(root.get("bill").get("paymentStatus"), status);
    }
}

