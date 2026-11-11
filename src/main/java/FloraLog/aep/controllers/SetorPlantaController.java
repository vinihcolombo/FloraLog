package FloraLog.aep.controllers;

import FloraLog.aep.models.SetorPlantaModel;
import FloraLog.aep.services.SetorPlantaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setor-plantas")
public class SetorPlantaController {

    @Autowired
    private SetorPlantaService setorPlantaService;

    @GetMapping
    public List<SetorPlantaModel> listarPorSetor(@RequestParam String setorId) {
        return setorPlantaService.listarPlantasDoSetor(setorId);
    }

    @PostMapping
    public ResponseEntity<SetorPlantaModel> vincular(@RequestBody SetorPlantaModel associacao) {
        SetorPlantaModel novaAssociacao = setorPlantaService.vincularPlantaAoSetor(
                associacao.getSetorId(),
                associacao.getPlantaId(),
                associacao.getUsuarioId()
        );
        return ResponseEntity.ok(novaAssociacao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        setorPlantaService.removerVinculo(id);
        return ResponseEntity.noContent().build();
    }
}