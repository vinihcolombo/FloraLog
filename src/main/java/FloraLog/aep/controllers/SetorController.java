package FloraLog.aep.controllers;

import FloraLog.aep.models.SetorModel;
import FloraLog.aep.repositories.SetorRepository;
import FloraLog.aep.services.SetorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setores")
public class SetorController {

    @Autowired
    private SetorService setorService;

    @Autowired
    private SetorRepository setorRepository;

    @GetMapping
    public List<SetorModel> listarPorUsuario(@RequestParam String usuarioId) {
        return setorService.listarPorUsuario(usuarioId);
    }


    @GetMapping("/{id}")
    public ResponseEntity<SetorModel> buscarPorId(@PathVariable String id) {
        return setorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SetorModel> criar(@RequestBody SetorModel setor) {
        SetorModel novoSetor = setorService.salvar(setor);
        return ResponseEntity.ok(novoSetor);
    }
}