package jobportalapplication.jobportalapplication.Repository;

import jobportalapplication.jobportalapplication.Entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepo extends JpaRepository<JobEntity, Long> {

    List<JobEntity> findByExperienceIgnoreCase(String experience);

    List<JobEntity> findByJobTypeIgnoreCase(String jobType);

    List<JobEntity> findByLocationIgnoreCase(String location);

    List<JobEntity> findByCompanyIgnoreCase(String company);


    List<JobEntity> findByJobTitleContainingIgnoreCase(String jobTitle);
}
