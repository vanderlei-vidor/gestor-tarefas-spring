package com.example.demo.service;

import com.example.demo.model.Task;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelService {

    public void gerarExcelTarefas(HttpServletResponse response, List<Task> tarefas) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Minhas Tarefas");

        // 1. Criar o Cabeçalho
        Row headerRow = sheet.createRow(0);
        String[] colunas = { "Título", "Descrição", "Status", "Prazo", "Criada em" };

        for (int i = 0; i < colunas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(colunas[i]);
            // Dica: você pode adicionar estilo de negrito aqui depois
        }

        // 2. Preencher os Dados
        int rowNum = 1;
        DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Task task : tarefas) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(task.getTitle());
            row.createCell(1).setCellValue(task.getDescription() != null ? task.getDescription() : "");
            row.createCell(2).setCellValue(task.getStatus().toString());

            // Data de Prazo
            String prazo = (task.getDueDate() != null) ? task.getDueDate().format(formatterData) : "";
            row.createCell(3).setCellValue(prazo);

            // Data de Criação
            String criacao = (task.getDataCriacao() != null) ? task.getDataCriacao().format(formatterHora) : "";
            row.createCell(4).setCellValue(criacao);
        }

        // 3. Auto-ajuste das colunas (faz as colunas alargarem sozinhas conforme o
        // texto)
        for (int i = 0; i < colunas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}