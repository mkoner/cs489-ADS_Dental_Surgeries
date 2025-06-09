package mkoner.ads_dental_surgeries.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class SurgeryServiceImpl implements SurgeryService {

    private final SurgeryRepository surgeryRepository;
    private final SurgeryMapper surgeryMapper;
    private final AddressMapper addressMapper;


    public List<SurgeryResponseDTO> getAllSurgeries() {
        log.debug("Fetching all surgeries");
        var surgeries = surgeryRepository.findAll().stream()
                .map(surgeryMapper::mapToSurgeryResponseDTO)
                .toList();
        log.info("Successfully retrieved {} surgeries", surgeries.size());
        return surgeries;
    }

    public SurgeryResponseDTO getSurgeryById(Long id) {
        log.debug("Fetching surgery by ID {}", id);
        var surgery = surgeryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Could not find surgery with ID {}", id);
                    return new ResourceNotFoundException("Surgery with id " + id + " not found");
                });
        log.info("Successfully retrieved surgery with ID {}", id);
        return surgeryMapper.mapToSurgeryResponseDTO(surgery);
    }

    public SurgeryResponseDTO saveSurgery(SurgeryRequestDTO surgery) {
        log.info("Attempting to save new surgery: {}", surgery);

        Surgery newSurgery = surgeryMapper.mapToSurgery(surgery);
        Surgery savedSurgery = surgeryRepository.save(newSurgery);

        log.info("Successfully saved surgery with ID: {}", savedSurgery.getSurgeryId());

        return surgeryMapper.mapToSurgeryResponseDTO(savedSurgery);
    }

    public void deleteSurgery(Long id) {
        log.info("Attempting to delete surgery with ID: {}", id);
        try {
            surgeryRepository.deleteById(id);
            log.info("Successfully deleted surgery with ID: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.warn("Failed to delete surgery with ID {} due to associated records: {}", id, e.getMessage());
            throw new BadRequestException("Deletion failed due to associated records");
        } catch (Exception e) {
            log.error("Unexpected error while deleting surgery with ID {}: {}", id, e.getMessage(), e);
            throw new BadRequestException("Deletion failed");
        }
    }


    @Override
    public SurgeryResponseDTO updateSurgery(Long id, SurgeryRequestDTO surgeryRequestDTO) {
        log.info("Attempting to update surgery with ID: {}", id);

        var existingSurgery = surgeryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Surgery with ID {} not found", id);
                    return new ResourceNotFoundException("Surgery with id " + id + " not found");
                });

        existingSurgery.setAddress(addressMapper.mapToAddress(surgeryRequestDTO.address()));
        existingSurgery.setPhoneNumber(surgeryRequestDTO.phoneNumber());
        existingSurgery.setName(surgeryRequestDTO.name());

        Surgery updatedSurgery = surgeryRepository.save(existingSurgery);

        log.info("Successfully updated surgery with ID: {}", id);
        return surgeryMapper.mapToSurgeryResponseDTO(updatedSurgery);
    }

    public Page<SurgeryResponseDTO> getFilteredSurgeriesWithPagination(SurgeryFilterDTO filterDTO, Pageable pageable) {
        log.debug("Fetching filtered surgeries: {}", filterDTO);
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
        log.info("Successfully retrieved {} surgeries on page {}", surgeries.getTotalElements(), pageable.getPageNumber() );
        return surgeries.map(surgeryMapper::mapToSurgeryResponseDTO);
    }


}

