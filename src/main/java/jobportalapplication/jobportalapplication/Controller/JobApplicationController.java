package jobportalapplication.jobportalapplication.Controller;

import jobportalapplication.jobportalapplication.Entity.JobApplication;
import jobportalapplication.jobportalapplication.Entity.JobEntity;
import jobportalapplication.jobportalapplication.Service.JobApplicationService;

import jobportalapplication.jobportalapplication.Service.Jobserviceimp;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobportal/jobs")
public class JobApplicationController {

    private final JobApplicationService service;
    private final Jobserviceimp jobservice;

    public JobApplicationController(JobApplicationService service, Jobserviceimp jobservice) {
        this.service = service;
        this.jobservice = jobservice;
    }

    // -------------------------------
    //  APPLY FOR A JOB  (PUBLIC)
    // -------------------------------
    @PostMapping("/apply")
    public JobApplication apply(@RequestBody JobApplication application) {
        return service.save(application);
    }

    // --------------------------------------------------
    //   ADMIN ONLY → GET ALL APPLICATIONS
    // --------------------------------------------------
    @GetMapping("/admin/applications")
    @PreAuthorize("hasRole('ADMIN')")
    public List<JobApplication> getAllApplications() {
        return service.findAllApplications();
    }

    @PostMapping("/admin/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public JobEntity uploadJob(@RequestBody JobEntity job) {
        return jobservice.uploadJobs(job);
    }
}
