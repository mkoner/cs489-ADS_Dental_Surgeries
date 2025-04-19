package mkoner.ads_dental_surgeries.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "dentists")
@Data
@NoArgsConstructor
public class Dentist extends User {
    @Column(length = 50)
    private String specialization;

    @OneToMany(mappedBy = "dentist")
    private List<Appointment> appointments;

    public Dentist(String firstName, String lastName, String number, String email, String password, String specialization, Role role) {
        super(firstName, lastName, number, email, password, role);
        this.specialization = specialization;
    }
}

