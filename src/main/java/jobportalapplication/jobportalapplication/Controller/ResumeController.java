package jobportalapplication.jobportalapplication.Controller;

import jobportalapplication.jobportalapplication.Entity.ResumeEntity;
import jobportalapplication.jobportalapplication.Service.PaymentService;
import jobportalapplication.jobportalapplication.Service.ResumeService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeService resumeService;
    private final PaymentService paymentService;

    public ResumeController(ResumeService resumeService, PaymentService paymentService) {
        this.resumeService = resumeService;
        this.paymentService = paymentService;
    }

    // ----------------------------------------------------
    // 1️⃣ GENERATE RESUME USING AI
    // ----------------------------------------------------
    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody Map<String, Object> req) throws Exception {
        Long userId = Long.valueOf(req.get("userId").toString());
        String prompt = req.get("prompt").toString();

        ResumeEntity resume = resumeService.generateResume(userId, prompt);

        return ResponseEntity.ok(Map.of(
                "resumeId", resume.getId(),
                "resumeText", resume.getResumeText()
        ));
    }

    // ----------------------------------------------------
    // 2️⃣ DOWNLOAD — LOCKED IF NOT PAID
    // ----------------------------------------------------
    @GetMapping("/download")
    public ResponseEntity<?> download(@RequestParam Long userId,
                                      @RequestParam Long resumeId) throws Exception {

        if (!paymentService.hasPaid(userId)) {
            return ResponseEntity.status(402)
                    .body(Map.of(
                            "error", "Please pay ₹99 to download the resume.",
                            "paid", false
                    ));
        }

        byte[] pdf = resumeService.downloadPDF(resumeId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resume.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // ----------------------------------------------------
    // 3️⃣ FRONTEND CHECK PAYMENT STATUS
    // ----------------------------------------------------
    @GetMapping("/payment-status")
    public ResponseEntity<?> paymentStatus(@RequestParam Long userId) {
        boolean paid = paymentService.hasPaid(userId);
        return ResponseEntity.ok(Map.of("paid", paid));
    }

    // ----------------------------------------------------
    // 4️⃣ MOCK PAYMENT (REMOVE AFTER RAZORPAY)
    // ----------------------------------------------------
    @PostMapping("/pay")
    public ResponseEntity<?> markPaid(@RequestBody Map<String, Object> req) {

        Long userId = Long.valueOf(req.get("userId").toString());
        paymentService.markPaid(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "₹99 payment recorded successfully.",
                "paid", true
        ));
    }
}
