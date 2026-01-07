package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.Usuario;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.PdfService;
import com.example.demo.service.ExcelService; // IMPORT NOVO
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
public class WebController {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private UsuarioRepository repositoryUsuario;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ExcelService excelService; // INJEÇÃO NOVA

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/login")
    public String login() {
        return "login"; // Abre o arquivo login.html
    }

    @GetMapping("/tarefas")
    public String renderizarPagina(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        // 1. Acha o usuário logado pelo e-mail
        String emailLogado = userDetails.getUsername();
        Usuario usuarioLogado = repositoryUsuario.findByEmail(emailLogado).get();

        model.addAttribute("nomeUsuario", usuarioLogado.getNome());
        // 2. Busca APENAS as tarefas deste usuário
        List<Task> tarefas = repository.findByUsuario(usuarioLogado);
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

    @GetMapping("/")
    public String home() {
        return "cadastrar"; // Abre o cadastrar.html quando acessar o link puro do site
    }

    @PostMapping("/tarefas/salvar")
    public String salvarTarefa(Task task, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        // 1. Acha o dono da tarefa
        String emailLogado = userDetails.getUsername();
        Usuario usuarioLogado = repositoryUsuario.findByEmail(emailLogado).get();

        // 2. Vincula o usuário à tarefa
        task.setUsuario(usuarioLogado);

        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }

        repository.save(task);
        ra.addFlashAttribute("mensagem", "Tarefa adicionada com sucesso!");
        return "redirect:/tarefas";
    }

    @GetMapping("/tarefas/excluir/{id}")
    public String excluirTarefa(@PathVariable Long id, RedirectAttributes ra) {
        repository.deleteById(id);
        ra.addFlashAttribute("mensagem", "Tarefa excluída com sucesso!");
        return "redirect:/tarefas";
    }

    @PostMapping("/tarefas/concluir/{id}")
    public String concluirTarefa(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        repository.findById(id).ifPresent(task -> {
            // Verifica se a tarefa pertence mesmo ao usuário logado
            if (task.getUsuario().getEmail().equals(userDetails.getUsername())) {
                task.setStatus(TaskStatus.DONE);
                repository.save(task);
            }
        });
        return "redirect:/tarefas";
    }

    @PostMapping("/tarefas/editar/{id}")
    public String editarTarefa(@PathVariable Long id, Task taskAtualizada,
            @AuthenticationPrincipal UserDetails userDetails) {
        repository.findById(id).ifPresent(taskExistente -> {
            // SEGURANÇA: Só edita se o usuário logado for o dono da tarefa
            if (taskExistente.getUsuario().getEmail().equals(userDetails.getUsername())) {
                taskExistente.setTitle(taskAtualizada.getTitle());
                taskExistente.setDescription(taskAtualizada.getDescription());
                taskExistente.setStatus(taskAtualizada.getStatus());
                taskExistente.setDueDate(taskAtualizada.getDueDate());

                repository.save(taskExistente);
            }
        });
        return "redirect:/tarefas";
    }

    @GetMapping("/tarefas/pdf")
    public void exportarParaPdf(HttpServletResponse response, @AuthenticationPrincipal UserDetails userDetails)
            throws IOException {
        Usuario usuarioLogado = repositoryUsuario.findByEmail(userDetails.getUsername()).get();
        List<Task> tarefas = repository.findByUsuario(usuarioLogado);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=tarefas.pdf");

        pdfService.gerarPdfTarefas(response, tarefas);
    }

    // MÉTODO QUE ESTAVA FALTANDO PARA O EXCEL
    @GetMapping("/tarefas/excel")
    public void exportarParaExcel(HttpServletResponse response, @AuthenticationPrincipal UserDetails userDetails)
            throws IOException {
        String emailLogado = userDetails.getUsername();
        Usuario usuarioLogado = repositoryUsuario.findByEmail(emailLogado).get();

        List<Task> tarefas = repository.findByUsuario(usuarioLogado);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=tarefas.xlsx");

        excelService.gerarExcelTarefas(response, tarefas);
    }

    @GetMapping("/cadastrar")
    public String paginaCadastro() {
        return "cadastrar"; // Vai abrir o arquivo cadastrar.html
    }

    @PostMapping("/cadastrar")
    public String registrarUsuario(Usuario usuario, Model model) {
        // 1. Verifica se o e-mail já está cadastrado
        if (repositoryUsuario.findByEmail(usuario.getEmail()).isPresent()) {
            model.addAttribute("erro", "Este e-mail já está em uso!");
            return "cadastrar"; // Volta para a tela de cadastro exibindo o erro
        }

        // 2. Criptografa e salva
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        repositoryUsuario.save(usuario);

        return "redirect:/login?sucesso";
    }

}