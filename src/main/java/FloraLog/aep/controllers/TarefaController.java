package FloraLog.aep.controllers;

import FloraLog.aep.models.TarefaModel;
import FloraLog.aep.services.TarefaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
@CrossOrigin(origins = "*")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @GetMapping
    public List<TarefaModel> listar(@RequestParam String setorPlantaId) {
        return tarefaService.listarPorInstancia(setorPlantaId);
    }

    @PostMapping
    public ResponseEntity<TarefaModel> criar(@RequestBody TarefaModel tarefa) {
        TarefaModel novaTarefa = tarefaService.salvar(tarefa);
        return ResponseEntity.status(201).body(novaTarefa);
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<TarefaModel> alternarConclusao(@PathVariable String id) {
        TarefaModel atualizada = tarefaService.alternarConclusao(id);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        tarefaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}