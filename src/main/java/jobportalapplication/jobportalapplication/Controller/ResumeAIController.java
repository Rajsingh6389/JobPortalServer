package jobportalapplication.jobportalapplication.Controller;

import jobportalapplication.jobportalapplication.Service.GeminiAIService;
import jobportalapplication.jobportalapplication.dto.AIPromptRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ai/resume")
@CrossOrigin(origins = "*")
public class ResumeAIController {

    @Autowired
    private GeminiAIService aiService;



    // -----------------------------------------------------------
    // FULL RESUME GENERATOR USING aiService.generateResume(PROMPT)
    // -----------------------------------------------------------
    @PostMapping("/generate")
    public ResponseEntity<String> generateFullResume(@RequestBody AIPromptRequest req) {

        try {
            if (req.getPrompt() == null || req.getPrompt().isBlank()) {
                return ResponseEntity.badRequest().body("Prompt is required");
            }

            // ⭐ THIS METHOD EXISTS IN GeminiAIService ⭐
            String result = aiService.generateResume(req.getPrompt());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("AI Error: " + e.getMessage());
        }
    }
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody AIPromptRequest req) {

        try {
            if (req.getPrompt() == null || req.getPrompt().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("reply", "Prompt is required"));
            }

            // Generic AI response
            String aiReply = aiService.generateChat(req.getPrompt());

            return ResponseEntity.ok(Map.of("reply", aiReply));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("reply", "AI Error: " + e.getMessage()));
        }
    }

}
