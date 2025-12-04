package jobportalapplication.jobportalapplication.Service;

import jobportalapplication.jobportalapplication.Entity.JobEntity;

import java.util.List;

public interface JobService {

    List<JobEntity> getAllJobs();

    List<JobEntity> findByJobTitle(String jobTitle);

    List<JobEntity> findByCompany(String company);

    List<JobEntity> findByExperience(String exp);

    List<JobEntity> findByLocation(String loc);

    List<JobEntity> findByJobType(String jobType);

    JobEntity findJobById(Long id);
    JobEntity uploadJobs(JobEntity job);
}
