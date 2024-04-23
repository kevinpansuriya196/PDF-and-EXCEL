package com.example.PDF.services;

import net.minidev.json.parser.ParseException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExcelService {

    public void createExcel(Map<String, Object> requestBody) throws IOException, ParseException, java.text.ParseException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Dynamic Data");

        List<Map<String, Object>> tableData = (List<Map<String, Object>>) requestBody.get("tableData");


        Set<String> uniqueHeaders = new LinkedHashSet<>();
        for (Map<String, Object> row : tableData) {
            uniqueHeaders.addAll(row.keySet());
        }


        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);


        Row headerRow = sheet.createRow(0);
        int headerIndex = 0;
        for (String header : uniqueHeaders) {
            Cell cell = headerRow.createCell(headerIndex++);
            cell.setCellValue(header);
            cell.setCellStyle(headerStyle);
        }
        DataFormat dataFormat = workbook.createDataFormat();
        CellStyle numericCellStyle = workbook.createCellStyle();
        numericCellStyle.setDataFormat(dataFormat.getFormat("0.00"));


        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(dataFormat.getFormat("dd-MM-yyyy"));


        int rowIndex = 1;
        for (Map<String, Object> rowData : tableData) {
            Row row = sheet.createRow(rowIndex++);
            int cellIndex = 0;

            for (String header : uniqueHeaders) {
                Cell cell = row.createCell(cellIndex++);
                Object value = rowData.get(header);

                if (value == null) {
                    cell.setCellValue("");
                } else if (header.equalsIgnoreCase("DOB")) {
                    Date dateValue = parseDate(value);
                    if (dateValue != null) {
                        cell.setCellValue(dateValue);
                        cell.setCellStyle(dateCellStyle);
                    } else {
                        cell.setCellValue("Invalid Date");
                    }
                } else if (value instanceof String) {
                    cell.setCellValue((String) value);
                } else if (value instanceof Integer) {
                    cell.setCellStyle(numericCellStyle);
                    cell.setCellValue((Integer) value);
                } else if (value instanceof Double) {
                    cell.setCellValue((Double) value);
                } else if (value instanceof Boolean) {
                    cell.setCellValue((Boolean) value ? "TRUE" : "FALSE");
                } else {
                    cell.setCellValue(value.toString());
                }
            }
        }


        try (FileOutputStream fileOut = new FileOutputStream("C:\\Users\\admin\\OneDrive\\Desktop\\New folder\\EmployeeData.xlsx")) {
            workbook.write(fileOut);
        }

        workbook.close();
    }

    private Date parseDate(Object value) throws java.text.ParseException {
        if (value instanceof Date) {
            return (Date) value;
        } else if (value instanceof String) {
            String dateString = (String) value;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            return dateFormat.parse(dateString);
        }
        return null;
    }
}