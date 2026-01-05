package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.PdfService;
import com.example.demo.service.ExcelService; // IMPORT NOVO
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.io.IOException;
import java.util.List;

@Controller
public class WebController {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ExcelService excelService; // INJEÇÃO NOVA

    @GetMapping("/tarefas")
    public String renderizarPagina(Model model) {
        List<Task> tarefas = repository.findAll();
        model.addAttribute("listaDeTarefas", tarefas);

        // Contagem usando o Enum diretamente (mais seguro)
        long pendentes = tarefas.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
        long fazendo = tarefas.stream().filter(t -> t.getStatus() == TaskStatus.DOING).count();
        long concluidas = tarefas.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();

        model.addAttribute("qtdPendentes", pendentes);
        model.addAttribute("qtdFazendo", fazendo);
        model.addAttribute("qtdConcluidas", concluidas);
        model.addAttribute("total", tarefas.size());

        return "index";
    }

    @PostMapping("/tarefas/salvar")
    public String salvarTarefa(Task task) {
        // Se por algum motivo o status vier nulo, o Java garante o TODO aqui
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }

        // Log para você ver no terminal se a tarefa chegou viva
        System.out.println("Salvando tarefa: " + task.getTitle() + " com status: " + task.getStatus());

        repository.save(task);
        return "redirect:/tarefas";
    }

    @GetMapping("/tarefas/excluir/{id}")
    public String excluirTarefa(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/tarefas";
    }

    @GetMapping("/tarefas/pdf")
    public void exportarParaPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=tarefas.pdf");
        List<Task> tarefas = repository.findAll();
        pdfService.gerarPdfTarefas(response, tarefas);
    }

    // MÉTODO QUE ESTAVA FALTANDO PARA O EXCEL
    @GetMapping("/tarefas/excel")
    public void exportarParaExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=tarefas.xlsx");
        List<Task> tarefas = repository.findAll();
        excelService.gerarExcelTarefas(response, tarefas);
    }

}