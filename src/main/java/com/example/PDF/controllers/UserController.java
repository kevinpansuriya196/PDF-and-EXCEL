package com.example.PDF.controllers;

import com.example.PDF.services.PdfService;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pdf")
public class UserController {
    @Autowired
    private PdfService pdfService;
    @PostMapping("/generatePdf")
    public ResponseEntity<byte[]> generatePdf(@RequestBody Map<String, Object> requestBody) {
        List<List<String>> tableData = (List<List<String>>) requestBody.get("tableData");
        Map<String, String> additionalInfo = (Map<String, String>) requestBody.get("additionalInfo");

         List<String[]> convertedTableData = new ArrayList<>();
        for (List<String> row : tableData) {
            convertedTableData.add(row.toArray(new String[0]));
        }

        try {
            ByteArrayInputStream pdfBytes = pdfService.createPdf(convertedTableData, additionalInfo);

            byte[] pdfByteArray = IOUtils.toByteArray(pdfBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "generated.pdf");

            return new ResponseEntity<>(pdfByteArray, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}