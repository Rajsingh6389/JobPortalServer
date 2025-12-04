package jobportalapplication.jobportalapplication.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "job_entity")
public class JobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobTitle;
    private String company;
    private int applicants;
    private String experience;   // Entry Level, Expert, Intermediate
    private String jobType;      // Full-Time, Part-Time
    private String location;
    private String packageAmount;
    private int postedDaysAgo;

    @Column(columnDefinition = "TEXT")
    private String description;
}
