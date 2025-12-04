package jobportalapplication.jobportalapplication.Controller;

import jobportalapplication.jobportalapplication.Entity.JobEntity;
import jobportalapplication.jobportalapplication.Service.JobService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobportal")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs")
    public List<JobEntity> getAllJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/jobs/title")
    public List<JobEntity> getByTitle(@RequestParam String q) {
        return jobService.findByJobTitle(q);
    }

    @GetMapping("/jobs/company/{company}")
    public List<JobEntity> getByCompany(@PathVariable String company) {
        return jobService.findByCompany(company);
    }

    @GetMapping("/jobs/experience/{exp}")
    public List<JobEntity> getByExperience(@PathVariable String exp) {
        return jobService.findByExperience(exp);
    }

    @GetMapping("/jobs/location/{loc}")
    public List<JobEntity> getByLocation(@PathVariable String loc) {
        return jobService.findByLocation(loc);
    }

    @GetMapping("/jobs/type/{type}")
    public List<JobEntity> getByType(@PathVariable String type) {
        return jobService.findByJobType(type);
    }
    @GetMapping("jobs/{id}")
    public JobEntity getById(@PathVariable Long id){
        return jobService.findJobById(id);
    }
}
