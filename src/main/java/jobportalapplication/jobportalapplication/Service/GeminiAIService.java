package jobportalapplication.jobportalapplication.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;

@Service
public class GeminiAIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final ObjectMapper mapper = new ObjectMapper();

    /* ============================================================
       1️⃣ RESUME GENERATOR METHOD
       ============================================================ */
    public String generateResume(String prompt) throws Exception {

        String finalPrompt = """
                You are an expert ATS resume generator.
                Write a professional resume with:
                - A summary
                - Skills section
                - Experience section (3 bullet points each)
                - Project section
                - Education
                - Certifications
                Make it clean, professional, and ATS optimized.

                User content:
                """ + prompt;

        return callGemini(finalPrompt);
    }

    /* ============================================================
       2️⃣ GENERAL CHAT AI METHOD (USED BY VOICE AGENT)
       ============================================================ */
    public String generateChat(String prompt) throws Exception {

        String chatPrompt = """
                You are a helpful and intelligent AI assistant.
                Answer clearly and concisely.
                Do NOT format like a resume.
                Respond conversationally.

                Question:
                """ + prompt;

        return callGemini(chatPrompt);
    }

    /* ============================================================
       CORE METHOD THAT CALLS GEMINI API
       ============================================================ */
    private String callGemini(String prompt) throws Exception {

        CloseableHttpClient client = HttpClients.createDefault();

        URI uri = URI.create(apiUrl + "?key=" + apiKey);
        System.out.println("Calling Gemini URL: " + uri);

        HttpPost post = new HttpPost(uri);
        post.addHeader("Content-Type", "application/json");

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        post.setEntity(new StringEntity(mapper.writeValueAsString(body)));

        try (var response = client.execute(post)) {

            if (response.getCode() != 200) {
                return "Gemini API Error Code: " + response.getCode();
            }

            var entity = response.getEntity();
            if (entity == null) return "Empty response from Gemini.";

            Map data = mapper.readValue(entity.getContent(), Map.class);

            System.out.println("Gemini RAW Response: " + data);

            List candidates = (List) data.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "Gemini Error: candidates empty.";
            }

            Map candidate = (Map) candidates.get(0);
            Map content = (Map) candidate.get("content");

            List parts = (List) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                return "Gemini Error: no response parts.";
            }

            StringBuilder result = new StringBuilder();
            for (Object p : parts) {
                Map part = (Map) p;
                Object text = part.get("text");
                if (text != null) result.append(text.toString()).append("\n");
            }

            String output = result.toString().trim();
            return output.isEmpty() ? "Gemini Error: Empty text returned." : output;
        }
    }
}
