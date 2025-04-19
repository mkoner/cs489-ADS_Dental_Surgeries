package mkoner.ads_dental_surgeries.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Column(nullable = false, length = 50)
    private String country;
    @Column(nullable = false, length = 50)
    private String city;
    @Column(length = 50)
    private String zipCode;
    @Column(length = 50)
    private String street;
}

