package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "tasks")
@Data
public class Task {

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO; // Valor padrão aqui

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
    }

    // Método para formatar a data na tela (Ex: 06/01/2026)
    public String getDataFormatada() {
        // Se a data for nula (tarefas antigas), retorna um aviso ou vazio
        if (this.dataCriacao == null) {
            return "Sem data";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return this.dataCriacao.format(formatter);
    }
}