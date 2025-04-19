package mkoner.ads_dental_surgeries.mapper;

import mkoner.ads_dental_surgeries.dto.money.MoneyDTO;
import mkoner.ads_dental_surgeries.model.Money;
import org.springframework.stereotype.Component;

@Component
public class MoneyMapper {
    public Money mapToMoney(MoneyDTO moneyDTO) {
        return new Money(
                moneyDTO.amount(),
                moneyDTO.currency(),
                moneyDTO.currencySymbol()
        );
    }

    public MoneyDTO mapToMoneyDTO(Money money) {
        return new MoneyDTO(
                money.getAmount(),
                money.getCurrency(),
                money.getCurrencySymbol()
        );
    }
}
