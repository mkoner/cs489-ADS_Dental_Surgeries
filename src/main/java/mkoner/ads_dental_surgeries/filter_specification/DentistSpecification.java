package mkoner.ads_dental_surgeries.filter_specification;

import mkoner.ads_dental_surgeries.model.Dentist;
import org.springframework.data.jpa.domain.Specification;

public class DentistSpecification {

    public static Specification<Dentist> hasFirstName(String firstName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<Dentist> hasLastName(String lastName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<Dentist> hasPhoneNumber(String phone) {
        return (root, query, cb) -> cb.equal(root.get("phoneNumber"), phone);
    }

    public static Specification<Dentist> hasEmail(String email) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("emailAddress")), email.toLowerCase());
    }

    public static Specification<Dentist> hasSpecialization(String specialization) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("specialization")), "%" + specialization.toLowerCase() + "%");
    }
}

