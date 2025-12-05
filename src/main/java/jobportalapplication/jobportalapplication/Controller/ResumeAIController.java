package jobportalapplication.jobportalapplication.Controller;

import jobportalapplication.jobportalapplication.Service.GeminiAIService;
import jobportalapplication.jobportalapplication.dto.AIPromptRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/resume")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ResumeAIController {

    private GeminiAIService aiService;

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
    // Helper builder
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
}
