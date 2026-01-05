package com.example.demo.service;

import com.example.demo.model.Task;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PdfService {

    public void gerarPdfTarefas(HttpServletResponse response, List<Task> tarefas) throws IOException {
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, response.getOutputStream());

        documento.open();

        // Estilização simples
        Font fonteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph titulo = new Paragraph("Relatório de Tarefas", fonteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(titulo);
        documento.add(new Paragraph(" ")); // Linha em branco

        // Adicionando as tarefas ao PDF
        for (Task task : tarefas) {
            documento.add(new Paragraph("Tarefa: " + task.getTitle()));
            documento.add(new Paragraph("Status: " + task.getStatus()));
            documento.add(new Paragraph("Prazo: " + task.getDueDate()));
            documento.add(new Paragraph("--------------------------------------------------"));
        }

        documento.close();
    }
}