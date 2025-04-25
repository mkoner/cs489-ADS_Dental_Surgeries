package mkoner.ads_dental_surgeries.service.impl;

import mkoner.ads_dental_surgeries.dto.address.AddressDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryFilterDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryRequestDTO;
import mkoner.ads_dental_surgeries.dto.surgery.SurgeryResponseDTO;
import mkoner.ads_dental_surgeries.exception.custom_exception.BadRequestException;
import mkoner.ads_dental_surgeries.exception.custom_exception.ResourceNotFoundException;
import mkoner.ads_dental_surgeries.mapper.AddressMapper;
import mkoner.ads_dental_surgeries.mapper.SurgeryMapper;
import mkoner.ads_dental_surgeries.model.Address;
import mkoner.ads_dental_surgeries.model.Surgery;
import mkoner.ads_dental_surgeries.repository.SurgeryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurgeryServiceImplTest {

    @Mock
    private SurgeryRepository surgeryRepository;

    @Mock
    private SurgeryMapper surgeryMapper;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private SurgeryServiceImpl surgeryService;

    @Test
    @DisplayName("Get all surgeries should return list of SurgeryDTOs")
    void testGetAllSurgeries() {
        List<Surgery> surgeries = List.of(new Surgery(), new Surgery());
        List<SurgeryResponseDTO> responseDTOs = List.of(new SurgeryResponseDTO(1L, "", "", null),
                new SurgeryResponseDTO(2L, "", "", null));

        when(surgeryRepository.findAll()).thenReturn(surgeries);
        when(surgeryMapper.mapToSurgeryResponseDTO(any(Surgery.class))).thenReturn(new SurgeryResponseDTO(1L, "", "", null));

        List<SurgeryResponseDTO> result = surgeryService.getAllSurgeries();

        assertEquals(2, result.size());
        verify(surgeryRepository).findAll();
    }

    @Test
    @DisplayName("Get surgery by id when surgery exists in in db")
    void testGetSurgeryById_Success() {
        Surgery surgery = new Surgery();
        SurgeryResponseDTO responseDTO = new SurgeryResponseDTO(1L, "", "", null);

        when(surgeryRepository.findById(1L)).thenReturn(Optional.of(surgery));
        when(surgeryMapper.mapToSurgeryResponseDTO(surgery)).thenReturn(responseDTO);

        SurgeryResponseDTO result = surgeryService.getSurgeryById(1L);

        assertNotNull(result);
        verify(surgeryRepository).findById(1L);
    }

    @Test
    @DisplayName("Get surgery by id should throw resource not found exception")
    void testGetSurgeryById_NotFound() {
        when(surgeryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> surgeryService.getSurgeryById(1L));
    }

    @Test
    @DisplayName("Should persist surgery in db and return surgeryResponseDTO")
    void testSaveSurgery() {
        SurgeryRequestDTO requestDTO = new SurgeryRequestDTO("surgery", "1234567890", new AddressDTO("US", "NY", "1235", "YT"));
        Surgery surgery = new Surgery();
        Surgery savedSurgery = new Surgery();
        SurgeryResponseDTO responseDTO = new SurgeryResponseDTO(1L,"surgery", "1234567890", new AddressDTO("US", "NY", "1235", "YT"));

        when(surgeryMapper.mapToSurgery(requestDTO)).thenReturn(surgery);
        when(surgeryRepository.save(surgery)).thenReturn(savedSurgery);
        when(surgeryMapper.mapToSurgeryResponseDTO(savedSurgery)).thenReturn(responseDTO);

        SurgeryResponseDTO result = surgeryService.saveSurgery(requestDTO);

        assertEquals(responseDTO, result);
    }

    @Test
    @DisplayName("Delete surgery by id on success should return nothing")
    void testDeleteSurgery_Success() {
        doNothing().when(surgeryRepository).deleteById(1L);

        assertDoesNotThrow(() -> surgeryService.deleteSurgery(1L));
        verify(surgeryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete surgery by id on DataIntegrityViolation should throw BadRequestException")
    void testDeleteSurgery_DataIntegrityViolation() {
        doThrow(DataIntegrityViolationException.class).when(surgeryRepository).deleteById(1L);

        assertThrows(BadRequestException.class, () -> surgeryService.deleteSurgery(1L));
    }

    @Test
    @DisplayName("Update Surgery on success should return SurgeryResponseDTO")
    void testUpdateSurgery() {
        Surgery existing = new Surgery();
        SurgeryRequestDTO requestDTO = new SurgeryRequestDTO("surgery", "1234567890", new AddressDTO("US", "NY", "1235", "YT"));
        Address address = new Address();
        SurgeryResponseDTO responseDTO = new SurgeryResponseDTO(1L, "surgery", "1234567890", new AddressDTO("US", "NY", "1235", "YT"));

        when(surgeryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(addressMapper.mapToAddress(requestDTO.address())).thenReturn(address);
        when(surgeryMapper.mapToSurgeryResponseDTO(existing)).thenReturn(responseDTO);

        SurgeryResponseDTO result = surgeryService.updateSurgery(1L, requestDTO);

        assertEquals(responseDTO, result);
        verify(surgeryRepository).findById(1L);
        verify(surgeryMapper).mapToSurgeryResponseDTO(existing);
    }

    @Test
    @DisplayName("Should return a page of SurgeryResponseDTO")
    void testGetFilteredSurgeriesWithPagination() {
        SurgeryFilterDTO filterDTO = new SurgeryFilterDTO("Smile Clinic", null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Surgery surgery = new Surgery();
        SurgeryResponseDTO responseDTO = new SurgeryResponseDTO(1L, "surgery", "1234567890", new AddressDTO("US", "NY", "1235", "YT"));

        Page<Surgery> page = new PageImpl<>(List.of(surgery));
        when(surgeryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(surgeryMapper.mapToSurgeryResponseDTO(surgery)).thenReturn(responseDTO);

        Page<SurgeryResponseDTO> result = surgeryService.getFilteredSurgeriesWithPagination(filterDTO, pageable);

        assertEquals(1, result.getContent().size());
    }
}
