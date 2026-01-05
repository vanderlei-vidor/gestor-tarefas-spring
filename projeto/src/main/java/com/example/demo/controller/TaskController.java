package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository repository;

    // LISTAR TODAS
    @GetMapping
    public List<Task> listar() {
        return repository.findAll();
    }

    // CRIAR
    @PostMapping
    public Task salvar(@RequestBody Task task) {
        return repository.save(task);
    }

    // ATUALIZAR (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Task> atualizar(@PathVariable Long id, @RequestBody Task taskAtualizada) {
        return repository.findById(id)
                .map(task -> {
                    task.setTitle(taskAtualizada.getTitle());
                    task.setDescription(taskAtualizada.getDescription());
                    task.setStatus(taskAtualizada.getStatus());
                    task.setDueDate(taskAtualizada.getDueDate());
                    Task atualizada = repository.save(task);
                    return ResponseEntity.ok(atualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETAR (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}