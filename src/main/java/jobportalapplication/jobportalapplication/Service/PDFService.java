package jobportalapplication.jobportalapplication.Service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PDFService {

    /**
     * Convert a plain text resume into a PDF using PDFBox.
     */
    public byte[] generatePdfFromText(String text) throws Exception {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            content.setFont(PDType1Font.HELVETICA, 11);

            float margin = 40;
            float yStart = 780;

            content.beginText();
            content.newLineAtOffset(margin, yStart);
            content.setLeading(14f);

            // Split text into lines to write properly
            String[] lines = text.split("\n");

            for (String line : lines) {

                // Wrap long lines to avoid overflow
                while (line.length() > 100) {
                    String part = line.substring(0, 100);
                    content.showText(part);
                    content.newLine();
                    line = line.substring(100);
                }

                content.showText(line);
                content.newLine();
            }

            content.endText();
            content.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);

            return baos.toByteArray();
        }
    }
}
