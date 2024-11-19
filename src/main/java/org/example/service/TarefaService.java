package org.example.service;

import org.example.model.Prioridade;
import org.example.model.Status;
import org.example.model.Tarefa;
import org.example.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    public TarefaService(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    public Tarefa criarTarefa(Tarefa tarefa) {
        tarefa.setStatus(Status.A_FAZER);
        return tarefaRepository.save(tarefa);
    }

    public List<Tarefa> listarTarefasPorStatus() {
        return tarefaRepository.findAll();
    }

    public Tarefa atualizarTarefa(Long id, Tarefa tarefaAtualizada) {
        Tarefa tarefaExistente = tarefaRepository.findById(id).orElse(null);

        if (tarefaExistente != null) {
            if (tarefaAtualizada.getTitulo() != null) {
                tarefaExistente.setTitulo(tarefaAtualizada.getTitulo());
            }
            if (tarefaAtualizada.getDescricao() != null) {
                tarefaExistente.setDescricao(tarefaAtualizada.getDescricao());
            }
            if (tarefaAtualizada.getPrioridade() != null) {
                tarefaExistente.setPrioridade(tarefaAtualizada.getPrioridade());
            }
            if (tarefaAtualizada.getDateLimite() != null) {
                tarefaExistente.setDateLimite(tarefaAtualizada.getDateLimite());
            }
            if (tarefaAtualizada.getStatus() != null) {
                tarefaExistente.setStatus(tarefaAtualizada.getStatus());
            }
            return tarefaRepository.save(tarefaExistente);
        }
        return null;
    }

    public String deletarTarefa(Long id) {
        tarefaRepository.deleteById(id);

        return "Tarefa deletada com sucesso";
    }

    public Tarefa MoverPrioridade(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id).orElse(null);

        if (tarefa != null) {
            switch (tarefa.getPrioridade()){
                case BAIXA:
                    tarefa.setPrioridade(Prioridade.MEDIA);
                    break;
                case MEDIA:
                    tarefa.setPrioridade(Prioridade.ALTA);
                    break;
                case ALTA:
                    tarefa.setPrioridade(Prioridade.BAIXA);
                    break;
            }
            return tarefaRepository.save(tarefa);
        } else {
            return null;
        }
    }

    public Tarefa MoverStatus(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id).orElse(null);

        if (tarefa != null) {
            switch (tarefa.getStatus()){
                case A_FAZER:
                    tarefa.setStatus(Status.EM_PROGRESSO);
                    break;
                case EM_PROGRESSO:
                    tarefa.setStatus(Status.CONCLUIDO);
                    break;
                case CONCLUIDO:
                    tarefa.setStatus(Status.A_FAZER);
                    break;
            }
            return tarefaRepository.save(tarefa);
        } else {
            return null;
        }
    }

    public Map<Status, List<Tarefa>> OrdenarPrioridade() {
        List<Tarefa> tarefas = tarefaRepository.findAll();

        return tarefas.stream()
                .collect(Collectors.groupingBy(
                        Tarefa::getStatus,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                lista -> lista.stream().sorted(Comparator.comparing(Tarefa::getPrioridade).reversed()).toList()
                        )
                ));
    }

    public List<Tarefa> filtrarPorPrioridade(Prioridade prioridade) {
        return tarefaRepository.findAll().stream()
                .filter(tarefa -> tarefa.getPrioridade() == prioridade)
                .collect(Collectors.toList());
    }

    public List<Tarefa> filtrarPorDataLimite(String dataLimite) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate filtroData = LocalDate.parse(dataLimite, formatter);

        return tarefaRepository.findAll().stream()
                .filter(tarefa -> {
                    try {
                        LocalDate tarefaDataLimite = LocalDate.parse(tarefa.getDateLimite(), formatter);
                        return !tarefaDataLimite.isBefore(filtroData);
                    } catch (DateTimeParseException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter ALTERNATIVE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Map<Status, Map<String, List<Tarefa>>> gerarRelatorio() {
        List<Tarefa> tarefas = tarefaRepository.findAll();

        return tarefas.stream()
                .collect(Collectors.groupingBy(
                        Tarefa::getStatus,
                        Collectors.groupingBy(tarefa -> {
                            try {
                                LocalDateTime dataLimite;
                                if (tarefa.getDateLimite() != null) {
                                    try {
                                        dataLimite = LocalDateTime.parse(tarefa.getDateLimite(), FORMATTER);
                                    } catch (DateTimeParseException e) {
                                        dataLimite = LocalDate.parse(tarefa.getDateLimite(), ALTERNATIVE_FORMAT).atStartOfDay();
                                    }

                                    if (dataLimite.isBefore(LocalDateTime.now())
                                            && !tarefa.getStatus().equals(Status.CONCLUIDO)) {
                                        return "Atrasadas";
                                    } else {
                                        return "No Prazo";
                                    }
                                } else {
                                    return "Sem Data Limite";
                                }
                            } catch (DateTimeParseException e) {
                                return "Formato Inválido";
                            }
                        })
                ));
    }
}
