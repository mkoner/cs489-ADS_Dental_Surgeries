package mkoner.ads_dental_surgeries.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
public class Patient extends User {
    private LocalDate dateOfBirth;

    @OneToMany(mappedBy = "patient")
    private List<Appointment> appointments;

    public Patient(String firstName, String lastName, String number, String email, String password, LocalDate dateOfBirth, Role role) {
        super(firstName, lastName, number, email, password, role);
        this.dateOfBirth = dateOfBirth;
    }
}

