package com.example.demo.service;

import com.example.demo.model.Task;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {

    public void gerarExcelTarefas(HttpServletResponse response, List<Task> tarefas) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Tarefas");

        // Criando o cabeçalho
        Row headerRow = sheet.createRow(0);
        String[] colunas = { "ID", "Título", "Descrição", "Status", "Prazo" };

        for (int i = 0; i < colunas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(colunas[i]);
            // Deixar o cabeçalho em negrito
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }

        // Preenchendo os dados
        int rowIdx = 1;
        for (Task task : tarefas) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(task.getId());
            row.createCell(1).setCellValue(task.getTitle());
            row.createCell(2).setCellValue(task.getDescription());
            row.createCell(3).setCellValue(task.getStatus().toString());
            row.createCell(4).setCellValue(task.getDueDate().toString());
        }

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}