package com.example.demo.service;

import com.example.demo.model.Task;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    public void gerarPdfTarefas(HttpServletResponse response, List<Task> tarefas) throws IOException {
        Document document = new Document(PageSize.A4.rotate()); // rotate() deixa a folha deitada (melhor para muitas
                                                                // colunas)
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        document.add(new Paragraph("Relatório de Tarefas - Gestor Pro"));
        document.add(new Paragraph(" ")); // Espaço em branco

        // Criamos a tabela com 5 colunas
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        // Definimos larguras proporcionais: Título(20%), Descrição(35%), Status(15%),
        // Prazo(15%), Criada em(15%)
        table.setWidths(new float[] { 2.0f, 3.5f, 1.5f, 1.5f, 1.5f });

        // Cabeçalhos
        table.addCell("Título");
        table.addCell("Descrição");
        table.addCell("Status");
        table.addCell("Prazo");
        table.addCell("Criada em");

        DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Task task : tarefas) {
            table.addCell(task.getTitle());
            table.addCell(task.getDescription() != null ? task.getDescription() : "-");
            table.addCell(task.getStatus().toString());

            // Formata o Prazo (LocalDate)
            table.addCell(task.getDueDate() != null ? task.getDueDate().format(formatterData) : "-");

            // Formata a Data de Criação (LocalDateTime)
            table.addCell(task.getDataCriacao() != null ? task.getDataCriacao().format(formatterHora) : "-");
        }

        document.add(table);
        document.close();
    }
}