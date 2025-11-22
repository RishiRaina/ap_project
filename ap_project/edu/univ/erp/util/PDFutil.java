package edu.univ.erp.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PDFutil {

    public static boolean writeTranscriptPDF(File file, String rollNo, List<String[]> rows) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            PDPageContentStream contentw = new PDPageContentStream(doc, page);//this is like the pen to write

            // Title
            contentw.beginText();
            contentw.setFont(PDType1Font.HELVETICA_BOLD, 22);
            contentw.newLineAtOffset(50, 770);
            contentw.showText("Transcript – Roll No: " + rollNo);
            contentw.endText();

            // Table rows
            int y = 730;
            for (String[] row : rows) {
                contentw.beginText();
                contentw.setFont(PDType1Font.HELVETICA, 12);
                contentw.newLineAtOffset(50, y);
                contentw.showText(String.join(" | ", row));
                contentw.endText();
                y -= 20;
            }

            contentw.close();
            doc.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeSectionStatsPDF(
            File file,
            int sectionId,
            String statsPlainText
    ) {
        try (PDDocument doc = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(doc, page);

            float marginLeft = 50;
            float y = 770;

            // ----- Title -----
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 22);
            cs.newLineAtOffset(marginLeft, y);
            cs.showText("Section Statistics – Section " + sectionId);
            cs.endText();

            y -= 40;

            cs.setFont(PDType1Font.HELVETICA, 12);

            // ----- Rewrite statsPlainText into multiple lines -----
            String[] lines = statsPlainText.split("\n");
            for (String line : lines) {

                // page break logic
                if (y < 70) {
                    cs.endText();
                    cs.close();

                    page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 12);
                    y = 770;
                    cs.newLineAtOffset(marginLeft, y);
                }

                cs.beginText();
                cs.newLineAtOffset(marginLeft, y);
                cs.showText(line);
                cs.endText();
                y -= 20;
            }

            cs.close();
            doc.save(file);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}
