package com.example.app.services;

import com.example.app.entities.Question;
import com.example.app.entities.Reponse;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PdfExportService {

    public String generateQuizReport(List<Question> questions, List<Reponse> reponses) {
        try {
            String userHome = System.getProperty("user.home");
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            Path downloadsPath = Paths.get(userHome, "Downloads", "Rapport_Quiz_" + timestamp + ".pdf");
            String dest = downloadsPath.toString();

            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(dest));
            document.open();

            // Font configurations
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Title
            Paragraph title = new Paragraph("Rapport Quiz Questions", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Summary Info
            String datestr = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
            document.add(new Paragraph("Date de génération : " + datestr, normalFont));
            document.add(new Paragraph("Total Questions : " + questions.size(), normalFont));
            document.add(new Paragraph("Total Réponses : " + reponses.size(), normalFont));
            document.add(new Paragraph("\n"));

            // Questions Table
            document.add(new Paragraph("Liste des Questions", subtitleFont));
            document.add(new Paragraph("\n"));

            PdfPTable qTable = new PdfPTable(3);
            qTable.setWidthPercentage(100);
            qTable.setWidths(new float[]{1f, 4f, 2f});

            addCell(qTable, "ID", headerFont);
            addCell(qTable, "Question", headerFont);
            addCell(qTable, "Créée le", headerFont);

            for (Question q : questions) {
                addCell(qTable, String.valueOf(q.getId()), normalFont);
                addCell(qTable, q.getQuestion(), normalFont);
                addCell(qTable, q.getCreatedAt() != null ? q.getCreatedAt().toString() : "N/A", normalFont);
            }
            document.add(qTable);
            document.add(new Paragraph("\n"));

            // Answers Table
            document.add(new Paragraph("Liste des Réponses", subtitleFont));
            document.add(new Paragraph("\n"));

            PdfPTable rTable = new PdfPTable(4);
            rTable.setWidthPercentage(100);
            rTable.setWidths(new float[]{1f, 3f, 2f, 3f});

            addCell(rTable, "ID", headerFont);
            addCell(rTable, "Option", headerFont);
            addCell(rTable, "Tag", headerFont);
            addCell(rTable, "Question", headerFont);

            for (Reponse r : reponses) {
                addCell(rTable, String.valueOf(r.getId()), normalFont);
                addCell(rTable, r.getOption(), normalFont);
                addCell(rTable, r.getTag(), normalFont);
                addCell(rTable, r.getQuestion() != null ? r.getQuestion().getQuestion() : "N/A", normalFont);
            }
            document.add(rTable);

            document.close();
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setPadding(5);
        table.addCell(cell);
    }
}
