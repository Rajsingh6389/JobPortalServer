package jobportalapplication.jobportalapplication.Service;



import jobportalapplication.jobportalapplication.Entity.ResumeEntity;
import jobportalapplication.jobportalapplication.Repository.ResumeRepository;
import org.springframework.stereotype.Service;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final GeminiAIService geminiAI;
    private final PDFService pdfService;

    public ResumeService(ResumeRepository resumeRepository, GeminiAIService geminiAI, PDFService pdfService) {
        this.resumeRepository = resumeRepository;
        this.geminiAI = geminiAI;
        this.pdfService = pdfService;
    }

    public ResumeEntity generateResume(Long userId, String prompt) throws Exception {
        String resumeText = geminiAI.generateResume(prompt);

        ResumeEntity entity = new ResumeEntity();
        entity.setUserId(userId);
        entity.setResumeText(resumeText);

        return resumeRepository.save(entity);
    }

    public byte[] downloadPDF(Long resumeId) throws Exception {
        ResumeEntity resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        return pdfService.generatePdfFromText(resume.getResumeText());
    }
}
