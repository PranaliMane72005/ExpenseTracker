package com.financetracker.service;

import com.financetracker.model.Export;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.repository.ExportRepository;
import com.financetracker.repository.TransactionRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@Service
public class ExportService {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private ExportRepository exportRepository;

    public byte[] exportToPdf(User user) throws DocumentException {
        List<Transaction> transactions = transactionRepository.findByUserOrderByDateDesc(user);

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Personal Finance Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        // Subtitle
        Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY);
        Paragraph sub = new Paragraph("User: " + user.getName() + " | " + user.getEmail(), subFont);
        sub.setAlignment(Element.ALIGN_CENTER);
        sub.setSpacingAfter(20);
        document.add(sub);

        // Table
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.5f, 1f, 2f, 1.5f, 1f, 2f});

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        String[] headers = {"Date", "Type", "Category", "Amount", "Account", "Description"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new BaseColor(55, 65, 81));
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        boolean alt = false;
        for (Transaction t : transactions) {
            BaseColor bg = alt ? new BaseColor(249, 250, 251) : BaseColor.WHITE;
            addCell(table, t.getDate().toString(), rowFont, bg);
            addCell(table, t.getType().name(), rowFont,
                    t.getType() == Transaction.TransactionType.INCOME
                            ? new BaseColor(220, 252, 231) : new BaseColor(254, 226, 226));
            addCell(table, t.getCategory().name(), rowFont, bg);
            addCell(table, user.getCurrency() + " " + t.getAmount(), rowFont, bg);
            addCell(table, t.getAccount() != null ? t.getAccount() : "-", rowFont, bg);
            addCell(table, t.getDescription() != null ? t.getDescription() : "-", rowFont, bg);
            alt = !alt;
        }

        document.add(table);
        document.close();

        logExport(user, Export.ExportFormat.PDF);
        return baos.toByteArray();
    }

    public String exportToCsv(User user) throws IOException {
        List<Transaction> transactions = transactionRepository.findByUserOrderByDateDesc(user);

        StringWriter sw = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT
                .withHeader("ID", "Date", "Type", "Category", "Amount", "Currency", "Account", "Description", "Created At"))) {
            for (Transaction t : transactions) {
                printer.printRecord(
                        t.getId(),
                        t.getDate(),
                        t.getType(),
                        t.getCategory(),
                        t.getAmount(),
                        user.getCurrency(),
                        t.getAccount(),
                        t.getDescription(),
                        t.getCreatedAt()
                );
            }
        }

        logExport(user, Export.ExportFormat.CSV);
        return sw.toString();
    }

    private void addCell(PdfPTable table, String text, Font font, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private void logExport(User user, Export.ExportFormat format) {
        exportRepository.save(Export.builder().user(user).format(format).build());
    }
}
