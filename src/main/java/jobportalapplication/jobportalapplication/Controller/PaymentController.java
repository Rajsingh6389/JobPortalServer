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

    /**
     * Create Cashfree Order
     */
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> req) {
        try {
            Integer amount = (Integer) req.getOrDefault("amount", 99);
            Long userId = Long.valueOf(String.valueOf(req.get("userId")));

            String receipt = "resume_" + userId + "_" + System.currentTimeMillis();

            // Create order via Cashfree service
            Map<String, Object> order = cashfreeService.createOrder(amount, "INR", receipt);

            return ResponseEntity.ok(Map.of(
                    "orderId", order.get("orderId"),
                    "amount", order.get("amount"),
                    "currency", order.get("currency"),
                    "clientId", cashfreeService.getClientId(), // similar to Razorpay keyId
                    "cashfreeResponse", order.get("cashfreeResponse")
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


    /**
     * Verify Cashfree Payment Status
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> payload) {

        try {
            Long userId = Long.valueOf(String.valueOf(payload.get("userId")));
            String orderId = (String) payload.get("orderId");

            // Call Cashfree to get order status
            String statusJson = cashfreeService.verifyPayment(orderId);

            // Simple check → If paid, unlock user
            boolean isPaid = statusJson.contains("\"order_status\":\"PAID\"");

            if (!isPaid) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Payment not completed",
                        "status", statusJson
                ));
            }

            // ✔ Update user payment status
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
