package jobportalapplication.jobportalapplication.Repository;
import jobportalapplication.jobportalapplication.Entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

}