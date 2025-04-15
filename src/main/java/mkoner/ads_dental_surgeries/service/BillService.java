package mkoner.ads_dental_surgeries.service;

import mkoner.ads_dental_surgeries.model.Bill;

import java.time.LocalDate;
import java.util.List;

public interface BillService {
    List<Bill> getAllBills();
    Bill getBillById(Long id);
    Bill saveBill(Bill bill);
    void deleteBill(Long id);
    List<Bill> getBillsByAppointment(Long appointmentId);
    List<Bill> getOverdueBills(LocalDate dueBefore);
}

