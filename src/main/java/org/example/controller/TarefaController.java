package org.example.controller;

import org.example.model.Prioridade;
import org.example.model.Status;
import org.example.model.Tarefa;
import org.example.service.TarefaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @PostMapping
    public Tarefa criarTarefa(@RequestBody Tarefa tarefa) {
        return tarefaService.criarTarefa(tarefa);
    }

    @GetMapping
    public List<Tarefa> listarTarefas() {
        return tarefaService.listarTarefasPorStatus();
    }

    @PutMapping("/atualizar/{id}")
    public Tarefa atualizarTarefa(@PathVariable Long id, @RequestBody Tarefa tarefaAtualizada) {
        return tarefaService.atualizarTarefa(id, tarefaAtualizada);
    }

    @DeleteMapping("/{id}")
    public String deletarTarefa(@PathVariable Long id) {
        return tarefaService.deletarTarefa(id);
    }

    @PutMapping("/prioridade/{id}")
    public Tarefa MoverPrioridade(@PathVariable Long id) {
        return tarefaService.MoverPrioridade(id);
    }

    @PutMapping("/status/{id}")
    public Tarefa MoverStatus(@PathVariable Long id) {
        return tarefaService.MoverStatus(id);
    }

    @PutMapping("/ordenarPrioridade")
    public Map<Status, List<Tarefa>> ordenarPrioridade() {
        return tarefaService.OrdenarPrioridade();
    }

    @PutMapping("/filtrarPrioridade")
    public ResponseEntity<?> filtrarPorPrioridade(@RequestParam(value = "prioridade", required = true) Prioridade prioridade) {
        if (prioridade == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensagem", "Nenhuma prioridade foi fornecida. Tente novamente com BAIXA, MEDIA ou ALTA."));
        }

        List<Tarefa> tarefasFiltradas = tarefaService.filtrarPorPrioridade(prioridade);

        if (tarefasFiltradas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensagem", "Nenhuma tarefa encontrada com a prioridade: " + prioridade));
        }

        return ResponseEntity.ok(tarefasFiltradas);
    }

    @PutMapping("/filtrarDataLimite")
    public ResponseEntity<?> filtrarPorDataLimite(@RequestParam(value = "data", required = true) String data) {
        if (data == null || data.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensagem", "Nenhuma data foi fornecida. Envie uma data no formato yyyy-MM-dd."));
        }

        try {
            List<Tarefa> tarefasFiltradas = tarefaService.filtrarPorDataLimite(data);

            if (tarefasFiltradas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensagem", "Nenhuma tarefa encontrada com data limite igual ou posterior a: " + data));
            }

            return ResponseEntity.ok(tarefasFiltradas);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensagem", "Formato de data inválido. Use o formato yyyy-MM-dd."));
        }
    }

    @GetMapping("/relatorio")
    public Map<Status, Map<String, List<Tarefa>>> gerarRelatorio() {
        return tarefaService.gerarRelatorio();
    }
}
