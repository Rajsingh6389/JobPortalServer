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

    /**
     * UNIVERSAL Gemini API Caller
     * Accepts a plain prompt and returns plain text
     */
    public String sendPrompt(String prompt) throws Exception {

        CloseableHttpClient client = HttpClients.createDefault();

        URI uri = URI.create(apiUrl + "?key=" + apiKey);

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

            return result.toString().trim();
        }
    }

    /**
     * Resume Section Generator
     */
    public String generateResumeSection(String section, String userPrompt) throws Exception {

        String prompt = """
                You are an expert ATS resume writer.
                Generate ONLY the %s section.
                Requirements:
                - Bullet points when appropriate
                - Short, factual statements
                - No stories
                - No extra sections
                - ATS-optimized text

                User Input:
                %s
                """.formatted(section.toUpperCase(), userPrompt);

        return sendPrompt(prompt);
    }

    /**
     * AI College Autocomplete
     */
    public String generateCollegeSuggestions(String query) throws Exception {

        String prompt = """
                You are an Indian College Autocomplete API.
                Return ONLY a JSON array of colleges that match the user query.

                User Query: "%s"

                Example Output:
                ["IIT Delhi", "Delhi University", "DTU"]
                """.formatted(query);

        String raw = sendPrompt(prompt);

        return cleanJsonArray(raw);
    }

    /**
     * Cleans AI output so frontend always receives valid JSON
     */
    public String cleanJsonArray(String raw) {
        int start = raw.indexOf("[");
        int end = raw.lastIndexOf("]");

        if (start != -1 && end != -1) {
            return raw.substring(start, end + 1).trim();
        }
        return "[]";
    }
}
