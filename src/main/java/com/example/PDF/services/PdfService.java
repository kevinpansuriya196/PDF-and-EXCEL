package com.example.PDF.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.pdfbox.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class PdfService {
    private Logger logger = LoggerFactory.getLogger(PdfService.class);

//    @Value("${image.path}")
//    private String imagePath;

    public ByteArrayInputStream createPdf(List<String[]> tableData, Map<String, String> additionalInfo) {
        logger.info("Create PDF started : ");
    
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, stream);
            writer.setPageEvent(new PdfHeader());
            document.open();

            addImageAndContent(document);
            addAdditionalInfo(document, additionalInfo);
            Chunk newLine1 = new Chunk("\n");
            Paragraph paragraph = new Paragraph();
            paragraph.add(newLine1);
            document.add(newLine1);
            PdfPTable table = new PdfPTable(4); // 4 columns
            addTableHeader(table);
            addRows(table, tableData);
            document.add(table);


            document.close();
        } catch (DocumentException | IOException e) {
            logger.error("Error creating PDF: {}", e.getMessage());
        }

        return new ByteArrayInputStream(stream.toByteArray());
    }

    private void addTableHeader(PdfPTable table) {

        Chunk newLine1 = new Chunk("\n");
        Paragraph paragraph = new Paragraph();
        paragraph.add(newLine1);
        Chunk newLine = new Chunk("\n");
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        PdfPCell cell = new PdfPCell();
        cell.setPhrase(new Phrase("NAME", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("CITY", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("DESIGNATION", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("SALARY", font));
        table.addCell(cell);
    }

    private void addRows(PdfPTable table, List<String[]> tableData) {
        // Add data rows
        for (String[] rowData : tableData) {
            for (String data : rowData) {
                table.addCell(data);
            }
        }
    }

    public void addImageAndContent(Document document) throws IOException, DocumentException {

        InputStream imageStream = getClass().getResourceAsStream("/image.jpg");


        if (imageStream != null) {
            byte[] imageData = StreamUtils.copyToByteArray(imageStream);

            Image image = Image.getInstance(imageData);
            image.scaleToFit(200, 200);
            Paragraph content = new Paragraph();
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

            content.add(new Chunk("Address: ABC," + "\n" + "surat," + "\n" + "Gujarat", font));

            PdfPTable imageTable = new PdfPTable(2);
            imageTable.setWidthPercentage(100);
            PdfPCell imageCell = new PdfPCell(image);
            imageCell.setBorder(Rectangle.NO_BORDER);
            imageTable.addCell(imageCell);

            PdfPCell contentCell = new PdfPCell(content);
            contentCell.setBorder(Rectangle.NO_BORDER);
            imageTable.addCell(contentCell);

            document.add(imageTable);

            logger.info("Image added to PDF.");
        } else {
            logger.error("Image file not found in the classpath.");
        }
    }

    private class PdfHeader extends PdfPageEventHelper {
        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            PdfPCell header = new PdfPCell(new Phrase("EMPLOY REPORT" + "\n\n\n", font));
            header.setBorder(0);
            header.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            header.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            headerTable.addCell(header);

            try {
                document.add(headerTable);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    }
    private void addAdditionalInfo(Document document, Map<String, String> additionalInfo) throws DocumentException {
        Paragraph additionalInfoParagraph = new Paragraph();
        additionalInfoParagraph.add(Chunk.NEWLINE);
        additionalInfoParagraph.add(new Chunk("Additional Information:", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        additionalInfoParagraph.add(Chunk.NEWLINE);

        for (Map.Entry<String, String> entry : additionalInfo.entrySet()) {
            additionalInfoParagraph.add(entry.getKey() + ": " + entry.getValue());
            additionalInfoParagraph.add(Chunk.NEWLINE);
        }

        document.add(additionalInfoParagraph);
    }
}
