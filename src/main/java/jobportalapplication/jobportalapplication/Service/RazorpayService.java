package jobportalapplication.jobportalapplication.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RazorpayService {

    private final RazorpayClient client;
    private final String keyId;
    private final String keySecret;

    public RazorpayService(
            @Value("${razorpay.key_id}") String keyId,
            @Value("${razorpay.key_secret}") String keySecret
    ) throws Exception {
        this.client = new RazorpayClient(keyId, keySecret);
        this.keyId = keyId;
        this.keySecret = keySecret;
    }

    public String getKeyId() {
        return this.keyId;
    }

    /**
     * Create Razorpay Order
     */
    public Map<String, Object> createOrder(int amountInRupees, String currency, String receipt) throws Exception {
        JSONObject request = new JSONObject();
        request.put("amount", amountInRupees * 100);  // Convert Rupees → paise
        request.put("currency", currency);
        request.put("receipt", receipt);
        request.put("payment_capture", 1);

        Order order = client.Orders.create(request);

        return Map.of(
                "orderId", order.get("id"),
                "amount", order.get("amount"),
                "currency", order.get("currency")
        );
    }

    /**
     * Verify Razorpay Signature
     */
    public boolean verifySignature(String orderId, String paymentId, String razorpaySignature) {
        String payload = orderId + "|" + paymentId;
        String expected = HmacUtils.hmacSha256Hex(keySecret, payload);
        return expected.equals(razorpaySignature);
    }
}
