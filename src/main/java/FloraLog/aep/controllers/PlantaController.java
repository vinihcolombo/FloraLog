package FloraLog.aep.controllers;

import FloraLog.aep.models.PlantaModel;
import FloraLog.aep.services.PlantaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/biblioteca")
@CrossOrigin(origins = "*")
public class PlantaController {

    @Autowired
    private PlantaService plantaService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PlantaModel>> listarBiblioteca(@PathVariable String usuarioId) {
        List<PlantaModel> plantas = plantaService.listarBibliotecaPorUsuario(usuarioId);
        return ResponseEntity.ok(plantas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantaModel> buscarPorId(@PathVariable String id) {
        try {
            PlantaModel planta = plantaService.buscarPorId(id);
            return ResponseEntity.ok(planta);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<PlantaModel> adicionarPlanta(@RequestBody PlantaModel planta) {
        PlantaModel novaPlanta = plantaService.salvar(planta);
        return ResponseEntity.status(201).body(novaPlanta);
    }

    @PostMapping("/lote")
    public ResponseEntity<List<PlantaModel>> adicionarVariasPlantas(@RequestBody List<PlantaModel> plantas) {
        List<PlantaModel> plantasSalvas = new ArrayList<>();
        for (PlantaModel planta : plantas) {
            plantasSalvas.add(plantaService.salvar(planta));
        }
        return ResponseEntity.status(201).body(plantasSalvas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantaModel> atualizarPlanta(@PathVariable String id, @RequestBody PlantaModel planta) {
        try {
            PlantaModel plantaAtualizada = plantaService.atualizar(id, planta);
            return ResponseEntity.ok(plantaAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPlanta(@PathVariable String id) {
        try {
            plantaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/importar/{codigo}")
    public ResponseEntity<?> importarPlantaPorCodigo(@PathVariable String codigo, @RequestParam String usuarioId) {
        try {
            PlantaModel novaPlanta = plantaService.importarPorCodigo(codigo, usuarioId);
            return ResponseEntity.status(201).body(novaPlanta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}