package jobportalapplication.jobportalapplication.Service;

import jobportalapplication.jobportalapplication.Entity.JobEntity;
import jobportalapplication.jobportalapplication.Repository.JobRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class Jobserviceimp implements JobService {

    private final JobRepo jobRepo;

    @Override
    public List<JobEntity> getAllJobs() {
        return jobRepo.findAll();
    }

    @Override
    public List<JobEntity> findByJobTitle(String jobTitle) {
        return jobRepo.findByJobTitleContainingIgnoreCase(jobTitle);
    }

    @Override
    public List<JobEntity> findByCompany(String company) {
        return jobRepo.findByCompanyIgnoreCase(company);
    }

    @Override
    public List<JobEntity> findByExperience(String exp) {
        return jobRepo.findByExperienceIgnoreCase(exp);
    }

    @Override
    public List<JobEntity> findByLocation(String loc) {
        return jobRepo.findByLocationIgnoreCase(loc);
    }

    @Override
    public List<JobEntity> findByJobType(String jobType) {
        return jobRepo.findByJobTypeIgnoreCase(jobType);
    }

    @Override
    public JobEntity findJobById(Long id) {
        return jobRepo.getById(id);
    }

    @Override
    public JobEntity uploadJobs(JobEntity job) {
        return jobRepo.save(job);
    }

}
