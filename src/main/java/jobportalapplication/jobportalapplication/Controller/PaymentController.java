package jobportalapplication.jobportalapplication.Controller;

import jobportalapplication.jobportalapplication.Repository.UserRepository;
import jobportalapplication.jobportalapplication.Service.RazorpayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final RazorpayService razorpayService;
    private final UserRepository userRepository;

    public PaymentController(RazorpayService razorpayService, UserRepository userRepository) {
        this.razorpayService = razorpayService;
        this.userRepository = userRepository;
    }

    /**
     * Create Razorpay Order
     */
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> req) {
        try {
            Integer amount = (Integer) req.getOrDefault("amount", 99);
            Long userId = Long.valueOf(String.valueOf(req.get("userId")));

            String receipt = "resume_" + userId + "_" + System.currentTimeMillis();

            var order = razorpayService.createOrder(amount, "INR", receipt);

            return ResponseEntity.ok(Map.of(
                    "orderId", order.get("orderId"),
                    "amount", order.get("amount"),
                    "currency", order.get("currency"),
                    "keyId", razorpayService.getKeyId()  // ✔ Send actual keyId
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verify Payment Signature
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> payload) {

        try {
            Long userId = Long.valueOf(String.valueOf(payload.get("userId")));
            String orderId = (String) payload.get("orderId");
            String paymentId = (String) payload.get("paymentId");
            String signature = (String) payload.get("signature");

            boolean valid = razorpayService.verifySignature(orderId, paymentId, signature);

            if (!valid) {
                return ResponseEntity.ok(Map.of("success", false, "message", "Invalid Signature"));
            }

            // ✔ Update user payment status
            userRepository.findById(userId).ifPresent(user -> {
                user.setPaymentStatus(true);
                user.setPaymentDate(LocalDateTime.now());
                userRepository.save(user);
            });

            return ResponseEntity.ok(Map.of("success", true));

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }
}
