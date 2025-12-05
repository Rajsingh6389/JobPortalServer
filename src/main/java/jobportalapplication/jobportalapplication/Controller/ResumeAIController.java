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

    // ----------------------------------------------------
    // SECTION-BASED AI GENERATION (summary, experience etc.)
    // ----------------------------------------------------
    @PostMapping("/{section}")
    public ResponseEntity<String> generateResumeSection(
            @PathVariable String section,
            @RequestBody AIPromptRequest req
    ) {
        try {

            if (req.getPrompt() == null || req.getPrompt().isBlank()) {
                return ResponseEntity.badRequest().body("Prompt is required");
            }

            // Use the new method in your GeminiAIService
            String result = aiService.generateResumeSection(section, req.getPrompt());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("AI Error: " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    // AI COLLEGE AUTOCOMPLETE ENDPOINT
    // ----------------------------------------------------
    @PostMapping("/college-suggest")
    public ResponseEntity<?> suggestColleges(@RequestBody Map<String, String> req) {

        try {
            String query = req.get("query");

            if (query == null || query.isBlank()) {
                return ResponseEntity.ok("[]");
            }

            // NEW: Use your Gemini AI service autocomplete method
            String cleanJson = aiService.generateCollegeSuggestions(query);

            return ResponseEntity.ok(cleanJson);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok("[]");
        }
    }
}
