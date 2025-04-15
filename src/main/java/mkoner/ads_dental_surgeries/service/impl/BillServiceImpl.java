package mkoner.ads_dental_surgeries.service.impl;

import mkoner.ads_dental_surgeries.model.Bill;
import mkoner.ads_dental_surgeries.repository.BillRepository;
import mkoner.ads_dental_surgeries.service.BillService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;

    public BillServiceImpl(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Bill getBillById(Long id) {
        return billRepository.findById(id).orElse(null);
    }

    public Bill saveBill(Bill bill) {
        return billRepository.save(bill);
    }

    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }

    public List<Bill> getBillsByAppointment(Long appointmentId) {
        return billRepository.findByAppointmentAppointmentId(appointmentId);
    }

    public List<Bill> getOverdueBills(LocalDate dueBefore) {
        return billRepository.findByDueDateBefore(dueBefore);
    }
}

