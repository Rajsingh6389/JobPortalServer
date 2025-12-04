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

    public String generateResume(String prompt) throws Exception {

        String finalPrompt = """
                You are an expert ATS resume generator.
                Write a professional resume with:
                - Summary
                - Skills
                - Experience (3 bullet points each)
                - Projects
                - Education
                - Certifications

                User prompt:
                """ + prompt;

        CloseableHttpClient client = HttpClients.createDefault();
        URI uri = URI.create(apiUrl + "?key=" + apiKey);

        HttpPost post = new HttpPost(uri);
        post.addHeader("Content-Type", "application/json");

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", finalPrompt)
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

            // NEW FORMAT
            List candidates = (List) data.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "Gemini Error: candidates empty (wrong model endpoint or invalid key).";
            }

            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");

            List parts = (List) content.get("parts");

            StringBuilder result = new StringBuilder();
            for (Object p : parts) {
                Map part = (Map) p;
                if (part.containsKey("text")) {
                    result.append(part.get("text").toString()).append("\n");
                }
            }

            if (result.toString().isBlank()) {
                return "Gemini Error: No text returned from model.";
            }

            return result.toString().trim();
        }
    }
}
