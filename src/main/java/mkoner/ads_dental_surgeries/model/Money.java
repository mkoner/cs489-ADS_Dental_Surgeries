package mkoner.ads_dental_surgeries.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.math.BigDecimal;

@Embeddable
@Data
public class Money {
    private BigDecimal amount;
    @Column(length = 3)
    private String currency;
    @Column(length = 3)
    private String currencySymbol;

    public Money() {}

    public Money(BigDecimal amount, String currency, String currencySymbol) {
        this.amount = amount;
        this.currency = currency;
        this.currencySymbol = currencySymbol;
    }
}

