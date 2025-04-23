package mkoner.ads_dental_surgeries.filter_specification;

import mkoner.ads_dental_surgeries.model.Surgery;
import org.springframework.data.jpa.domain.Specification;

public class SurgerySpecification {

    public static Specification<Surgery> hasName(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Surgery> hasPhoneNumber(String phone) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("phoneNumber")), "%" + phone.toLowerCase() + "%");
    }

    public static Specification<Surgery> hasCity(String city) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("address").get("city")), "%" + city.toLowerCase() + "%");
    }

    public static Specification<Surgery> hasCountry(String country) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("address").get("country")), "%" + country.toLowerCase() + "%");
    }
}

