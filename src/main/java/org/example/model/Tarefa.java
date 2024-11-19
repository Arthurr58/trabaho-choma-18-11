package org.example.model;

import jakarta.persistence.*;

@Entity
public class Tarefa {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String titulo;
    private String descricao;
    private String date;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    private String dateLimite;

    @PrePersist
    protected void onCreate() {
        this.status = Status.A_FAZER;
        this.prioridade = Prioridade.BAIXA;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(Object titulo) {
        this.titulo = (String) titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(Object descricao) {
        this.descricao = (String) descricao;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Object prioridade) {
        this.prioridade = (Prioridade) prioridade;
    }

    public String getDateLimite() {
        return dateLimite;
    }

    public void setDateLimite(Object dateLimite) {
        this.dateLimite = (String) dateLimite;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

