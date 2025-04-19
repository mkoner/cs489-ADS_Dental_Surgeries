package mkoner.ads_dental_surgeries.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "surgeries")
@Data
@NoArgsConstructor
public class Surgery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surgeryId;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "surgery")
    private List<Appointment> appointments;

    public Surgery(String name, String number) {
        this.name = name;
        this.phoneNumber = number;
    }
}

