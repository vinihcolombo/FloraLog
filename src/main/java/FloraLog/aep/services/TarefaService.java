package FloraLog.aep.services;

import FloraLog.aep.models.TarefaModel;
import FloraLog.aep.repositories.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    public List<TarefaModel> listarPorInstancia(String setorPlantaId) {
        return tarefaRepository.findBySetorPlantaId(setorPlantaId);
    }

    public TarefaModel salvar(TarefaModel tarefa) {
        return tarefaRepository.save(tarefa);
    }

    public TarefaModel alternarConclusao(String id) {
        TarefaModel tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        tarefa.setConcluida(!tarefa.isConcluida());
        return tarefaRepository.save(tarefa);
    }

    public void deletar(String id) {
        tarefaRepository.deleteById(id);
    }
}