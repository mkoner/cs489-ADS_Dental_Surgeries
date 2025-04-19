package mkoner.ads_dental_surgeries.service.impl;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.bill.BillRequestDTO;
import mkoner.ads_dental_surgeries.dto.bill.BillResponseDTO;
import mkoner.ads_dental_surgeries.mapper.BillMapper;
import mkoner.ads_dental_surgeries.model.Bill;
import mkoner.ads_dental_surgeries.repository.AppointmentRepository;
import mkoner.ads_dental_surgeries.repository.BillRepository;
import mkoner.ads_dental_surgeries.service.BillService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final AppointmentRepository appointmentRepository;
    private final BillMapper billMapper;

    public List<BillResponseDTO> getAllBills() {
        return billRepository.findAll().stream()
                .map(billMapper::mapToBillResponseDTO).toList();
    }

    public BillResponseDTO getBillById(Long id) {
        var bill = billRepository.findById(id).orElse(null);
        return billMapper.mapToBillResponseDTO(bill);
    }

//    public BillResponseDTO saveBill(BillRequestDTO billRequestDTO) {
//        var bill = billMapper.mapToBill(billRequestDTO);
//        var appointment = appointmentRepository.findById(billRequestDTO.appointmentId()).orElse(null);
//        bill.setAppointment(appointment);
//        return billMapper.mapToBillResponseDTO(billRepository.save(bill));
//    }

    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }

    public List<BillResponseDTO> getBillsByAppointment(Long appointmentId) {
        return billRepository.findByAppointmentAppointmentId(appointmentId).stream()
                .map(billMapper::mapToBillResponseDTO).toList();
    }

    public List<BillResponseDTO> getOverdueBills(LocalDate dueBefore) {
        return billRepository.findByDueDateBefore(dueBefore).stream()
                .map(billMapper::mapToBillResponseDTO).toList();
    }
}

