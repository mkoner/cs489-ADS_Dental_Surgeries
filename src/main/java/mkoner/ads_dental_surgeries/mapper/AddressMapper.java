package mkoner.ads_dental_surgeries.mapper;

import mkoner.ads_dental_surgeries.dto.address.AddressDTO;
import mkoner.ads_dental_surgeries.model.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public Address mapToAddress(AddressDTO addressDTO) {
        return new Address(
                addressDTO.country(),
                addressDTO.city(),
                addressDTO.zipCode(),
                addressDTO.street()
        );
    }
    public AddressDTO mapToAddressDTO(Address address) {
        return new AddressDTO(
                address.getCountry(),
                address.getCity(),
                address.getZipCode(),
                address.getStreet()
        );
    }
}
