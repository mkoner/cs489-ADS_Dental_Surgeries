package mkoner.ads_dental_surgeries.mapper;

import lombok.RequiredArgsConstructor;
import mkoner.ads_dental_surgeries.dto.bill.BillRequestDTO;
import mkoner.ads_dental_surgeries.dto.bill.BillResponseDTO;
import mkoner.ads_dental_surgeries.dto.payment.PaymentResponseDTO;
import mkoner.ads_dental_surgeries.model.Bill;
import mkoner.ads_dental_surgeries.model.Money;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BillMapper {
    private final PaymentMapper paymentMapper;
    private final MoneyMapper moneyMapper;


    public BillResponseDTO mapToBillResponseDTO(Bill bill) {
        if(bill == null) {
            return null;
        }
        return new BillResponseDTO(
                bill.getBillId(),
                moneyMapper.mapToMoneyDTO(bill.getAmount()),
                bill.getDueDate(),
                bill.getDateOfBilling(),
                bill.getPayments().stream().map(paymentMapper::mapToPaymentResponseDTO).toList()
        );
    }
    public Bill mapToBill(BillRequestDTO billRequestDTO) {
        var money = new Money(billRequestDTO.amount(),
                billRequestDTO.currency(),
                billRequestDTO.currencySymbol());
        var bill = new Bill();
        bill.setDueDate(billRequestDTO.dueDate());
        bill.setAmount(money);
        bill.setDateOfBilling(LocalDate.now());
        return bill;
    }
}
