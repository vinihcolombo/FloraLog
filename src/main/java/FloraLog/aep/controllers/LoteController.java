package FloraLog.aep.controllers;

import FloraLog.aep.models.LoteModel;
import FloraLog.aep.services.LoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    @GetMapping
    public ResponseEntity<List<LoteModel>> listarTodos() {
        List<LoteModel> lotes = loteService.listarTodos();
        return ResponseEntity.ok(lotes);
    }

    @GetMapping("/horta/{hortaId}")
    public ResponseEntity<List<LoteModel>> listarPorHorta(@PathVariable String hortaId) {
        List<LoteModel> lotes = loteService.listarPorHorta(hortaId);
        return ResponseEntity.ok(lotes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteModel> buscarPorId(@PathVariable String id) {
        LoteModel lote = loteService.buscarPorId(id);
        return ResponseEntity.ok(lote);
    }

    @PostMapping
    public ResponseEntity<LoteModel> cadastrar(@RequestBody LoteModel lote) {
        LoteModel loteCriado = loteService.cadastrar(lote);
        return ResponseEntity.status(HttpStatus.CREATED).body(loteCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoteModel> atualizar(
            @PathVariable String id,
            @RequestBody LoteModel lote
    ) {
        LoteModel loteAtualizado = loteService.atualizar(id, lote);
        return ResponseEntity.ok(loteAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        loteService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}