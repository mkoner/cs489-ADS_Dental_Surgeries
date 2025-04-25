package mkoner.ads_dental_surgeries.service.impl;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryFilterDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryRequestDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryResponseDTO;
import mkoner.ads_dental_surgeries.exception.custom_exception.BadRequestException;
import mkoner.ads_dental_surgeries.exception.custom_exception.ResourceNotFoundException;
import mkoner.ads_dental_surgeries.filter_specification.SurgerySpecification;
import mkoner.ads_dental_surgeries.mapper.AddressMapper;
import mkoner.ads_dental_surgeries.mapper.SurgeryMapper;
import mkoner.ads_dental_surgeries.model.Surgery;
import mkoner.ads_dental_surgeries.repository.SurgeryRepository;
import mkoner.ads_dental_surgeries.service.SurgeryService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
        try{
            surgeryRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            System.out.println(e);
            throw new BadRequestException("Deletion failed due to associated records");
        }
        catch (Exception e){
            System.out.println(e);
            throw new BadRequestException("Deletion failed");
        }
    }


    @Override
    public SurgeryResponseDTO updateSurgery(Long id, SurgeryRequestDTO surgeryRequestDTO) {
        var existingSurgery = surgeryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Surgery with id " + id + " not found"));
        existingSurgery.setAddress(addressMapper.mapToAddress(surgeryRequestDTO.address()));
        existingSurgery.setPhoneNumber(surgeryRequestDTO.phoneNumber());
        existingSurgery.setName(surgeryRequestDTO.name());
        return surgeryMapper.mapToSurgeryResponseDTO(existingSurgery);
    }

    public Page<SurgeryResponseDTO> getFilteredSurgeriesWithPagination(SurgeryFilterDTO filterDTO, Pageable pageable) {
        Specification<Surgery> spec = Specification.where(null);

        if (filterDTO.name() != null) {
            spec = spec.and(SurgerySpecification.hasName(filterDTO.name()));
        }
        if (filterDTO.phoneNumber() != null) {
            spec = spec.and(SurgerySpecification.hasPhoneNumber(filterDTO.phoneNumber()));
        }
        if (filterDTO.city() != null) {
            spec = spec.and(SurgerySpecification.hasCity(filterDTO.city()));
        }
        if (filterDTO.country() != null) {
            spec = spec.and(SurgerySpecification.hasCountry(filterDTO.country()));
        }

        Page<Surgery> surgeries = surgeryRepository.findAll(spec, pageable);
        return surgeries.map(surgeryMapper::mapToSurgeryResponseDTO);
    }


}

