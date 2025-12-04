package jobportalapplication.jobportalapplication.dto;

// SignupRequest.java
import lombok.*;

@Data
@Getter
@Setter
public class SignupRequest {
    private String name;
    private String email;
    private String password;
}