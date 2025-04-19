package mkoner.ads_dental_surgeries.mapper;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryRequestDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryResponseDTO;
import mkoner.ads_dental_surgeries.model.Surgery;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurgeryMapper{
    private final AddressMapper addressMapper;
    public Surgery mapToSurgery(SurgeryRequestDTO surgeryRequestDTO) {
        var surgery = new Surgery(surgeryRequestDTO.name(), surgeryRequestDTO.phoneNumber());
        surgery.setAddress(addressMapper.mapToAddress(surgeryRequestDTO.address()));
        return surgery;
    }
    public SurgeryResponseDTO mapToSurgeryResponseDTO(Surgery surgery) {
        return new SurgeryResponseDTO(
                surgery.getSurgeryId(),
                surgery.getName(),
                surgery.getPhoneNumber(),
                addressMapper.mapToAddressDTO(surgery.getAddress())
        );
    }
}
