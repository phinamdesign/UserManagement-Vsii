package com.vsii.model;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class Write {
    public static final int COLUMN_INDEX_ID             = 0;
    public static final int COLUMN_INDEX_DATA           = 1;
    public static final int COLUMN_INDEX_FILE_NAME      = 2;
    public static final int COLUMN_INDEX_FILE_TYPE      = 3;
    public static final int COLUMN_INDEX_SIZE           = 4;
    public static final int COLUMN_INDEX_UPLOAD_TIME    = 5;
    private static CellStyle cellStyleFormatNumber = null;
    public static void writeExcel(List<DBFile> dbFiles, String excelFilePath) throws IOException {
        // Create Workbook
        Workbook workbook = getWorkbook(excelFilePath);

        // Create sheet
        Sheet sheet = workbook.createSheet("DBFiles"); // Create sheet with sheet name

        int rowIndex = 0;

        // Write header
        writeHeader(sheet, rowIndex);

        // Write data
        rowIndex++;
        for (DBFile dbFile : dbFiles) {
            // Create row
            Row row = sheet.createRow(rowIndex);
            // Write data on row
            writeBook(dbFile, row);
            rowIndex++;
        }

        // Auto resize column witdth
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        autosizeColumn(sheet, numberOfColumn);

        // Create file excel
        createOutputFile(workbook, excelFilePath);
        System.out.println("Done!!!");
    }

    // Create workbook
    public static Workbook getWorkbook(String excelFilePath) throws IOException {
        Workbook workbook = null;

        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook();
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook();
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }

        return workbook;
    }

    // Write header with format
    public static void writeHeader(Sheet sheet, int rowIndex) {
        // create CellStyle
        CellStyle cellStyle = createStyleForHeader(sheet);

        // Create row
        Row row = sheet.createRow(rowIndex);

        // Create cells
        Cell cell = row.createCell(COLUMN_INDEX_ID);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Id");

        cell = row.createCell(COLUMN_INDEX_DATA);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Data");

        cell = row.createCell(COLUMN_INDEX_FILE_NAME);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("File name");

        cell = row.createCell(COLUMN_INDEX_FILE_TYPE);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("File type");

        cell = row.createCell(COLUMN_INDEX_SIZE);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Size");

        cell = row.createCell(COLUMN_INDEX_UPLOAD_TIME);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Upload time");
    }

    // Write data
    public static void writeBook(DBFile dbFile, Row row) {
        if (cellStyleFormatNumber == null) {
            // Format number
            short format = (short)BuiltinFormats.getBuiltinFormat("#,##0");
            // DataFormat df = workbook.createDataFormat();
            // short format = df.getFormat("#,##0");

            //Create CellStyle
            Workbook workbook = row.getSheet().getWorkbook();
            cellStyleFormatNumber = workbook.createCellStyle();
            cellStyleFormatNumber.setDataFormat(format);
        }

        Cell cell = row.createCell(COLUMN_INDEX_ID);
        cell.setCellValue(dbFile.getId());

        cell = row.createCell(COLUMN_INDEX_FILE_NAME);
        cell.setCellValue(dbFile.getFileName());

        cell = row.createCell(COLUMN_INDEX_FILE_TYPE);
        cell.setCellValue(dbFile.getFileType());

        cell = row.createCell(COLUMN_INDEX_SIZE);
        cell.setCellValue(dbFile.getSize());
        cell.setCellStyle(cellStyleFormatNumber);

        cell = row.createCell(COLUMN_INDEX_UPLOAD_TIME);
        cell.setCellValue(dbFile.getUploadTime().toString());
    }

    // Create CellStyle for header
    private static CellStyle createStyleForHeader(Sheet sheet) {
        // Create font
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setBold(true);
        font.setFontHeightInPoints((short) 14); // font size
        font.setColor(IndexedColors.WHITE.getIndex()); // text color

        // Create CellStyle
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        return cellStyle;
    }

    // Auto resize column width
    private static void autosizeColumn(Sheet sheet, int lastColumn) {
        for (int columnIndex = 0; columnIndex < lastColumn; columnIndex++) {
            sheet.autoSizeColumn(columnIndex);
        }
    }

    // Create output file
    private static void createOutputFile(Workbook workbook, String excelFilePath) throws IOException {
        try (OutputStream os = new FileOutputStream(excelFilePath)) {
            workbook.write(os);
        }
    }
}
