package FloraLog.aep.controllers;

import FloraLog.aep.models.HortaModel;
import FloraLog.aep.services.HortaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hortas")
public class HortaController {

    private final HortaService hortaService;

    public HortaController(HortaService hortaService) {
        this.hortaService = hortaService;
    }

    @GetMapping
    public ResponseEntity<List<HortaModel>> listarTodas() {
        List<HortaModel> hortas = hortaService.listarTodas();
        return ResponseEntity.ok(hortas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HortaModel> buscarPorId(@PathVariable String id) {
        HortaModel horta = hortaService.buscarPorId(id);
        return ResponseEntity.ok(horta);
    }

    @PostMapping
    public ResponseEntity<HortaModel> cadastrar(@RequestBody HortaModel horta) {
        HortaModel hortaCriada = hortaService.cadastrar(horta);
        return ResponseEntity.status(HttpStatus.CREATED).body(hortaCriada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HortaModel> atualizar(
            @PathVariable String id,
            @RequestBody HortaModel horta
    ) {
        HortaModel hortaAtualizada = hortaService.atualizar(id, horta);
        return ResponseEntity.ok(hortaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        hortaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}