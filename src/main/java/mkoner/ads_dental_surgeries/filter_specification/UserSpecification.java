package mkoner.ads_dental_surgeries.filter_specification;

import mkoner.ads_dental_surgeries.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<User> hasLastName(String lastName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<User> hasPhoneNumber(String phoneNumber) {
        return (root, query, cb) -> cb.equal(root.get("phoneNumber"), phoneNumber);
    }

    public static Specification<User> hasEmailAddress(String emailAddress) {
        return (root, query, cb) -> cb.equal(root.get("emailAddress"), emailAddress);
    }

    public static Specification<User> hasRoleName(String roleName) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("role").get("name")), roleName.toLowerCase());
    }
}

