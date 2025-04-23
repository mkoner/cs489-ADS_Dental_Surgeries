package mkoner.ads_dental_surgeries.filter_specification;

import mkoner.ads_dental_surgeries.model.Patient;
import org.springframework.data.jpa.domain.Specification;

public class PatientSpecification {

    public static Specification<Patient> hasFirstName(String firstName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<Patient> hasLastName(String lastName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<Patient> hasPhoneNumber(String phone) {
        return (root, query, cb) -> cb.equal(root.get("phoneNumber"), phone);
    }

    public static Specification<Patient> hasEmail(String email) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("emailAddress")), email.toLowerCase());
    }

    public static Specification<Patient> hasCity(String city) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("address").get("city")), "%" + city.toLowerCase() + "%");
    }

    public static Specification<Patient> hasCountry(String country) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("address").get("country")), "%" + country.toLowerCase() + "%");
    }
}

