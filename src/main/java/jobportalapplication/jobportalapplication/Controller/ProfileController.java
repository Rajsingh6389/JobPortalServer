    package jobportalapplication.jobportalapplication.Controller;
    import jobportalapplication.jobportalapplication.Service.UserService;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.context.SecurityContextHolder;
    import jobportalapplication.jobportalapplication.dto.UpdateProfileRequest;
    import jobportalapplication.jobportalapplication.Entity.User;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    import org.springframework.web.bind.annotation.*;

    import java.util.Optional;

    @RestController
    @RequestMapping("/api/profile")
    public class ProfileController {

        private final UserService service;

        public ProfileController(UserService service) {
            this.service = service;
        }

        @GetMapping
        public ResponseEntity<?> getProfile() {
            String email = (String) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            User user = service.getUserByEmail(email);
            return ResponseEntity.ok(user);
        }

        @PutMapping("/update")
        public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest req) {

            String email = (String) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            User updated = service.updateProfile(email, req);
            return ResponseEntity.ok(updated);
        }

        @GetMapping("/user/{id}")
        public Optional<User> getUserById(@PathVariable Long id){
            return service.getUserById(id);
        }
    }
