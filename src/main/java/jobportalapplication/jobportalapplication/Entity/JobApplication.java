package jobportalapplication.jobportalapplication.Entity;


import jakarta.persistence.*;
import lombok.Data;



@Entity
@Data
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Job Information
    private Long jobId;
    private String jobTitle;
    private String company;
    private String location;
    private String jobType;
    private String packageAmount;
    private String experience;

    // Applicant Information
    private String name;
    private String email;
    private String phone;
    private String resumeLink;
}