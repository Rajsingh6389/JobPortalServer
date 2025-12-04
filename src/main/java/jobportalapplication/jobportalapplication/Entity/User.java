package jobportalapplication.jobportalapplication.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false, unique = true)
    private String email;

    @Column(nullable=false)
    private String password; // hashed

    private String about;
    private String role;
    private String location;
    private String expectedSalary;
    private String skills;
    private String userType;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean paymentStatus = false;

    private LocalDateTime paymentDate;

    // Optional: Allow future subscription upgrades
    private Integer resumeCredits = 0;
    // ADMIN or USER

// comma-separated string
    // role, createdAt, etc. add as needed
}