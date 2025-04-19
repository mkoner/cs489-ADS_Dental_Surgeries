package mkoner.ads_dental_surgeries.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(unique = true, nullable = false, length = 15)
    private String phoneNumber;
    @Column(unique = true, nullable = false)
    private String emailAddress;
    @Column(nullable = false)
    private String password;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "role_id")
    private Role role;

    public User(String firstName, String lastName, String number, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = number;
        this.emailAddress = email;
        this.password = password;
        this.role = role;
    }
}

