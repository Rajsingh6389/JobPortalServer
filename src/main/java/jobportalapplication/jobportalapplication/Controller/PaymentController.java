package jobportalapplication.jobportalapplication.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    // ⭐ CREATE ORDER (LIVE MODE → ALWAYS USE HTTPS)
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> req) {
        try {
            Integer amount = (Integer) req.getOrDefault("amount", 1);
            Long userId = Long.valueOf(String.valueOf(req.get("userId")));

            String receipt = "order_" + userId + "_" + System.currentTimeMillis();

            // 🔥 ALWAYS HTTPS — localhost not allowed in LIVE Cashfree
            String returnUrl = "https://jobportalbyrrr.netlify.app/dreamjob?order_id=" + receipt;

            Map<String, Object> order = cashfreeService.createOrder(
                    amount,
                    "INR",
                    receipt,
                    returnUrl
            );

            return ResponseEntity.ok(order);

        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }

    // ⭐ VERIFY PAYMENT & UPDATE DB
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(String.valueOf(payload.get("userId")));
            String orderId = String.valueOf(payload.get("orderId"));

            // 🔥 FIXED: removed typo (`orderI   d`)
            String raw = cashfreeService.verifyPayment(orderId);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(raw);

            String orderStatus = json.get("order_status").asText();
            String paymentStatus = json.has("payment_status")
                    ? json.get("payment_status").asText()
                    : "";

            boolean paid =
                    "PAID".equalsIgnoreCase(orderStatus) ||
                            "SUCCESS".equalsIgnoreCase(paymentStatus);

            if (!paid) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "orderStatus", orderStatus,
                        "paymentStatus", paymentStatus
                ));
            }

            // ⭐ UPDATE USER IN DATABASE
            userRepository.findById(userId).ifPresent(user -> {
                user.setPaymentStatus(true);
                user.setPaymentDate(LocalDateTime.now());
                userRepository.save(user);
            });

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "orderStatus", orderStatus,
                    "paymentStatus", paymentStatus
            ));

        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }
}
