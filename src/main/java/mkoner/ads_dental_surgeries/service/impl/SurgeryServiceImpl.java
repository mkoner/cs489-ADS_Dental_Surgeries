package mkoner.ads_dental_surgeries.service.impl;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryRequestDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryResponseDTO;
import mkoner.ads_dental_surgeries.exception.BadRequestException;
import mkoner.ads_dental_surgeries.exception.ResourceNotFoundException;
import mkoner.ads_dental_surgeries.mapper.AddressMapper;
import mkoner.ads_dental_surgeries.mapper.SurgeryMapper;
import mkoner.ads_dental_surgeries.model.Surgery;
import mkoner.ads_dental_surgeries.repository.SurgeryRepository;
import mkoner.ads_dental_surgeries.service.SurgeryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurgeryServiceImpl implements SurgeryService {

    private final SurgeryRepository surgeryRepository;
    private final SurgeryMapper surgeryMapper;
    private final AddressMapper addressMapper;


    public List<SurgeryResponseDTO> getAllSurgeries() {
        return surgeryRepository.findAll().stream()
                .map(surgeryMapper::mapToSurgeryResponseDTO)
                .collect(Collectors.toList());
    }

    public SurgeryResponseDTO getSurgeryById(Long id) {
        var surgery = surgeryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Surgery with id " + id + " not found"));
        return surgeryMapper.mapToSurgeryResponseDTO(surgery);
    }

    public SurgeryResponseDTO saveSurgery(SurgeryRequestDTO surgery) {
        Surgery newSurgery = surgeryMapper.mapToSurgery(surgery);
        return surgeryMapper.mapToSurgeryResponseDTO(surgeryRepository.save(newSurgery));
    }

    public void deleteSurgery(Long id) {
        surgeryRepository.deleteById(id);
    }

    public List<SurgeryResponseDTO> findByCity(String city) {
        return surgeryRepository.findSurgeriesByAddressCityIgnoreCase(city).stream()
                .map(surgeryMapper::mapToSurgeryResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SurgeryResponseDTO updateSurgery(Long id, SurgeryRequestDTO surgeryRequestDTO) {
        var existingSurgery = surgeryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Surgery with id " + id + " not found"));
        existingSurgery.setAddress(addressMapper.mapToAddress(surgeryRequestDTO.address()));
        existingSurgery.setPhoneNumber(surgeryRequestDTO.phoneNumber());
        existingSurgery.setName(surgeryRequestDTO.name());
        return surgeryMapper.mapToSurgeryResponseDTO(existingSurgery);
    }
}

