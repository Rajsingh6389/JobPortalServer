package jobportalapplication.jobportalapplication.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CashfreeService {

    private final String clientId;
    private final String clientSecret;
    private final String env;

    public CashfreeService(
            @Value("${cashfree.client_id}") String clientId,
            @Value("${cashfree.client_secret}") String clientSecret,
            @Value("${cashfree.env}") String env
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.env = env;
    }

    private String getBaseUrl() {
        return env.equalsIgnoreCase("prod")
                ? "https://api.cashfree.com/pg/orders"
                : "https://sandbox.cashfree.com/pg/orders";
    }

    public Map<String, Object> createOrder(
            int amountInRupees,
            String currency,
            String receipt,
            String returnUrl
    ) throws Exception {

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(getBaseUrl());

        post.addHeader("x-client-id", clientId);
        post.addHeader("x-client-secret", clientSecret);
        post.addHeader("x-api-version", "2023-08-01");
        post.addHeader("Content-Type", "application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("order_id", receipt);
        body.put("order_amount", (double) amountInRupees);
        body.put("order_currency", currency);

        // ⭐ Correct location for return_url
        Map<String, Object> meta = new HashMap<>();
        meta.put("return_url", returnUrl);
        body.put("order_meta", meta);

        // ⭐ Customer info — you can pass actual user values later
        Map<String, Object> customer = new HashMap<>();
        customer.put("customer_id", receipt);
        customer.put("customer_email", "unknown@gmail.com");
        customer.put("customer_phone", "9999999999");  // ✔ 10 digits

        body.put("customer_details", customer);

        ObjectMapper mapper = new ObjectMapper();
        post.setEntity(new StringEntity(mapper.writeValueAsString(body)));

        var response = client.execute(post);
        String responseJson = new String(response.getEntity().getContent().readAllBytes());

        return Map.of(
                "orderId", receipt,
                "amount", amountInRupees,
                "currency", currency,
                "cashfreeResponse", responseJson
        );
    }

    public String verifyPayment(String orderId) throws Exception {
        String url = getBaseUrl() + "/" + orderId;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);

        get.addHeader("x-client-id", clientId);
        get.addHeader("x-client-secret", clientSecret);
        get.addHeader("x-api-version", "2023-08-01");

        var response = client.execute(get);
        return new String(response.getEntity().getContent().readAllBytes());
    }
}
