package jobportalapplication.jobportalapplication.Service;


import jobportalapplication.jobportalapplication.Entity.JobApplication;
import jobportalapplication.jobportalapplication.Repository.JobApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobApplicationService {

    private final JobApplicationRepository repo;

    public JobApplicationService(JobApplicationRepository repo) {
        this.repo = repo;
    }

    public JobApplication save(JobApplication app) {
        return repo.save(app);
    }

    public List<JobApplication> findAllApplications(){
        return repo.findAll();
    }
}
