package jobportalapplication.jobportalapplication.Service;

import jobportalapplication.jobportalapplication.Entity.User;
import jobportalapplication.jobportalapplication.Repository.UserRepository;
import jobportalapplication.jobportalapplication.Security.JwtUtil;
import jobportalapplication.jobportalapplication.dto.AuthRequest;
import jobportalapplication.jobportalapplication.dto.AuthResponse;
import jobportalapplication.jobportalapplication.dto.SignupRequest;
import jobportalapplication.jobportalapplication.dto.UpdateProfileRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
 

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ---------------------------------------------------------
    //  SIGNUP
    // ---------------------------------------------------------
    public AuthResponse signup(SignupRequest req) {

        if (repo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .userType("USER")
                .paymentStatus(false)
                .build();

        User saved = repo.save(user);

        // ⭐ TOKEN WITH EMAIL + ROLE
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getUserType());

        return new AuthResponse(
                token,
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getUserType()
        );
    }

    // ---------------------------------------------------------
    //  LOGIN
    // ---------------------------------------------------------
    public AuthResponse login(AuthRequest req) {

        User user = repo.findByEmail(req.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException("Email not registered. Please sign up first.")
                );

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // ⭐ TOKEN WITH EMAIL + ROLE
        String token = jwtUtil.generateToken(user.getEmail(), user.getUserType());

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUserType()
        );
    }

    // ---------------------------------------------------------
    //  GET USER BY EMAIL
    // ---------------------------------------------------------
    public User getUserByEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    // ---------------------------------------------------------
    //  UPDATE PROFILE
    // ---------------------------------------------------------
    public User updateProfile(String email, UpdateProfileRequest req) {

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (req.getName() != null) user.setName(req.getName());
        if (req.getAbout() != null) user.setAbout(req.getAbout());
        if (req.getRole() != null) user.setRole(req.getRole());
        if (req.getLocation() != null) user.setLocation(req.getLocation());
        if (req.getExpectedSalary() != null) user.setExpectedSalary(req.getExpectedSalary());
        if (req.getSkills() != null) user.setSkills(req.getSkills());

        // ⭐ IMPORTANT — update admin role too

        return repo.save(user);
    }


    // ---------------------------------------------------------
    //  GET ALL USERS
    // ---------------------------------------------------------
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public Optional<User> getUserById(Long id){
        return repo.findById(id);
    }
}
