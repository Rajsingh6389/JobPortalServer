package jobportalapplication.jobportalapplication.Controller;

import jobportalapplication.jobportalapplication.Service.GeminiAIService;
import jobportalapplication.jobportalapplication.dto.AIPromptRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai/resume")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ResumeAIController {

    private final GeminiAIService aiService;

    // ------------------------------
    // GENERIC SECTION HANDLER
    // ------------------------------
    @PostMapping("/{section}")
    public ResponseEntity<String> generateResumeSection(
            @PathVariable String section,
            @RequestBody AIPromptRequest req
    ) {

        try {

            if (req.getPrompt() == null || req.getPrompt().isBlank()) {
                return ResponseEntity.badRequest().body("Prompt is required");
            }

            String finalPrompt = buildSectionPrompt(section, req.getPrompt());
            String result = aiService.generateResume(finalPrompt);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("AI Error: " + e.getMessage());
        }
    }

    // ------------------------------
    // AI COLLEGE AUTOCOMPLETE ENDPOINT
    // ------------------------------
    @PostMapping("/college-suggest")
    public ResponseEntity<?> suggestColleges(@RequestBody Map<String, String> req) {

        try {
            String query = req.get("query");

            if (query == null || query.isBlank()) {
                return ResponseEntity.ok("[]");
            }

            String prompt = """
                    You are an Indian College Autocomplete API.
                    Your job: return ONLY a JSON array of college names matching the user's text.
                    - No explanation
                    - No additional text
                    - No formatting outside JSON array
                    - Do NOT include numbers or ranking
                    
                    User input: "%s"
                    
                    Return format example:
                    ["Delhi University", "IIT Delhi", "DTU"]
                    """.formatted(query);

            String aiResponse = aiService.generateResume(prompt);

            // Ensure valid JSON array
            String cleanJson = cleanJsonArray(aiResponse);

            return ResponseEntity.ok(cleanJson);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok("[]");
        }
    }

    // ------------------------------
    // Prompt builder for resume sections
    // ------------------------------
    private String buildSectionPrompt(String section, String userPrompt) {
        return """
                You are an expert ATS Resume Generator.
                Generate ONLY the %s section of the resume.

                Requirements:
                - Clean formatted text
                - No storytelling, strictly resume style
                - Bullet points when needed
                - Short, factual sentences
                - ATS-friendly wording

                User Input:
                %s
                """.formatted(section.toUpperCase(), userPrompt);
    }

    // ------------------------------
    // Clean JSON from AI output
    // Ensures frontend can safely parse
    // ------------------------------
    private String cleanJsonArray(String raw) {

        int start = raw.indexOf("[");
        int end = raw.lastIndexOf("]");

        if (start != -1 && end != -1) {
            return raw.substring(start, end + 1).trim();
        }

        return "[]";
    }
}
