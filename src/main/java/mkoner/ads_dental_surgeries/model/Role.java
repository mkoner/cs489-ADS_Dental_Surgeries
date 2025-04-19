package mkoner.ads_dental_surgeries.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "roles")
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    @Column(nullable = false, unique = true, length = 50)
    private String roleName;
    public Role(String roleName) {
        this.roleName = roleName;
    }
}

