package jobportalapplication.jobportalapplication.Service;

import jobportalapplication.jobportalapplication.Entity.User;
import jobportalapplication.jobportalapplication.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private UserRepository userRepository;

    public boolean hasPaid(Long userId) {
        return userRepository.findById(userId)
                .map(User::getPaymentStatus)
                .orElse(false);
    }

    public void markPaid(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setPaymentStatus(true);
            user.setPaymentDate(LocalDateTime.now());
            userRepository.save(user);
        });
    }
}
