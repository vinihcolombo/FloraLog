package FloraLog.aep.controllers;

import FloraLog.aep.models.PlantaModel;
import FloraLog.aep.services.PlantaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plantas")
public class PlantaController {

    private final PlantaService plantaService;

    public PlantaController(PlantaService plantaService) {
        this.plantaService = plantaService;
    }


    @GetMapping
    public ResponseEntity<List<PlantaModel>> listarTodas() {

        List<PlantaModel> plantas =
                plantaService.listarTodas();

        return ResponseEntity.ok(plantas);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PlantaModel> buscarPorId(
            @PathVariable String id
    ) {

        PlantaModel planta =
                plantaService.buscarPorId(id);

        return ResponseEntity.ok(planta);
    }


    @PostMapping
    public ResponseEntity<PlantaModel> cadastrar(
            @RequestBody PlantaModel planta
    ) {

        PlantaModel plantaCriada =
                plantaService.cadastrar(planta);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(plantaCriada);
    }


    @PutMapping("/{id}")
    public ResponseEntity<PlantaModel> atualizar(
            @PathVariable String id,
            @RequestBody PlantaModel planta
    ) {

        PlantaModel plantaAtualizada =
                plantaService.atualizar(id, planta);

        return ResponseEntity.ok(plantaAtualizada);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable String id
    ) {

        plantaService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}