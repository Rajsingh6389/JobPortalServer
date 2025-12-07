package jobportalapplication.jobportalapplication.Controller;

import jobportalapplication.jobportalapplication.Repository.UserRepository;
import jobportalapplication.jobportalapplication.Service.CashfreeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final CashfreeService cashfreeService;
    private final UserRepository userRepository;

    public PaymentController(CashfreeService cashfreeService, UserRepository userRepository) {
        this.cashfreeService = cashfreeService;
        this.userRepository = userRepository;
    }

    // ⭐ CREATE CASHFREE ORDER
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> req) {
        try {
            Integer amount = (Integer) req.getOrDefault("amount", 99);
            Long userId = Long.valueOf(String.valueOf(req.get("userId")));

            String receipt = "resume_" + userId + "_" + System.currentTimeMillis();

            // Detect Prod vs Localhost
            String frontendUrl =
                    System.getenv("ENV") != null && System.getenv("ENV").equalsIgnoreCase("prod")
                            ? "https://jobportalbyrrr.netlify.app/dreamjob?order_id={order_id}"
                            : "http://localhost:5173/dreamjob?order_id={order_id}";

            Map<String, Object> order = cashfreeService.createOrder(
                    amount,
                    "INR",
                    receipt,
                    frontendUrl
            );

            return ResponseEntity.ok(Map.of(
                    "orderId", order.get("orderId"),
                    "amount", order.get("amount"),
                    "currency", order.get("currency"),
                    "clientId", cashfreeService.getClientId(),
                    "cashfreeResponse", order.get("cashfreeResponse")
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ⭐ VERIFY CASHFREE PAYMENT
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> payload) {

        try {
            Long userId = Long.valueOf(String.valueOf(payload.get("userId")));
            String orderId = (String) payload.get("orderId");

            String statusJson = cashfreeService.verifyPayment(orderId);

            boolean isPaid = statusJson.contains("\"order_status\":\"PAID\"");

            if (!isPaid) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Payment not completed",
                        "status", statusJson
                ));
            }

            // Update user as Premium
            userRepository.findById(userId).ifPresent(user -> {
                user.setPaymentStatus(true);
                user.setPaymentDate(LocalDateTime.now());
                userRepository.save(user);
            });

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Payment verified successfully",
                    "status", statusJson
            ));

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }
}
