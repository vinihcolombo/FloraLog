package FloraLog.aep.controllers;

import FloraLog.aep.models.*;
import FloraLog.aep.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ==========================================
    // ENDPOINTS DE UTILIZADOR
    // ==========================================

    @GetMapping
    public ResponseEntity<List<UsuarioModel>> listarTodos() {
        List<UsuarioModel> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioModel> buscarPorId(@PathVariable String id) {
        UsuarioModel usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioModel> buscarPorEmail(@PathVariable String email) {
        UsuarioModel usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioModel> login(@RequestBody UsuarioModel dadosLogin) {
        UsuarioModel usuarioAutenticado = usuarioService.autenticar(dadosLogin.getEmail(), dadosLogin.getSenha());
        return ResponseEntity.ok(usuarioAutenticado);
    }

    @PostMapping
    public ResponseEntity<UsuarioModel> cadastrar(@RequestBody UsuarioModel usuario) {
        UsuarioModel usuarioCriado = usuarioService.cadastrar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioModel> atualizar(
            @PathVariable String id,
            @RequestBody UsuarioModel usuario
    ) {
        UsuarioModel usuarioAtualizado = usuarioService.atualizar(id, usuario);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // ENDPOINTS DA BIBLIOTECA DE PLANTAS
    // ==========================================

    @GetMapping("/{usuarioId}/biblioteca")
    public ResponseEntity<List<PlantaModel>> listarBiblioteca(@PathVariable String usuarioId) {
        List<PlantaModel> plantas = usuarioService.listarBiblioteca(usuarioId);
        return ResponseEntity.ok(plantas);
    }

    @PostMapping("/{usuarioId}/biblioteca")
    public ResponseEntity<PlantaModel> adicionarPlantaBiblioteca(
            @PathVariable String usuarioId,
            @RequestBody PlantaModel planta
    ) {
        PlantaModel novaPlanta = usuarioService.adicionarPlantaBiblioteca(usuarioId, planta);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaPlanta);
    }

    @PutMapping("/{usuarioId}/biblioteca/{plantaId}")
    public ResponseEntity<PlantaModel> atualizarPlantaBiblioteca(
            @PathVariable String usuarioId,
            @PathVariable String plantaId,
            @RequestBody PlantaModel planta
    ) {
        PlantaModel atualizada = usuarioService.atualizarPlantaBiblioteca(usuarioId, plantaId, planta);
        return ResponseEntity.ok(atualizada);
    }

    @PostMapping("/{usuarioId}/biblioteca/importar/{codigo}")
    public ResponseEntity<?> importarPlantaPorCodigo(
            @PathVariable String usuarioId,
            @PathVariable String codigo
    ) {
        try {
            PlantaModel novaPlanta = usuarioService.importarPlantaPorCodigo(usuarioId, codigo);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaPlanta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{usuarioId}/biblioteca/{plantaId}")
    public ResponseEntity<Void> removerPlantaBiblioteca(
            @PathVariable String usuarioId,
            @PathVariable String plantaId
    ) {
        usuarioService.removerPlantaBiblioteca(usuarioId, plantaId);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // ENDPOINTS DE SETORES
    // ==========================================

    @GetMapping("/{usuarioId}/setores")
    public ResponseEntity<List<SetorModel>> listarSetores(@PathVariable String usuarioId) {
        List<SetorModel> setores = usuarioService.listarSetores(usuarioId);
        return ResponseEntity.ok(setores);
    }

    @PostMapping("/{usuarioId}/setores")
    public ResponseEntity<SetorModel> adicionarSetor(
            @PathVariable String usuarioId,
            @RequestBody SetorModel setor
    ) {
        SetorModel novoSetor = usuarioService.adicionarSetor(usuarioId, setor);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoSetor);
    }

    @DeleteMapping("/{usuarioId}/setores/{setorId}")
    public ResponseEntity<Void> removerSetor(
            @PathVariable String usuarioId,
            @PathVariable String setorId
    ) {
        usuarioService.removerSetor(usuarioId, setorId);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // ENDPOINTS DE PLANTAS NO SETOR
    // ==========================================

    @PostMapping("/{usuarioId}/setores/{setorId}/plantas")
    public ResponseEntity<SetorPlantaModel> adicionarPlantaNoSetor(
            @PathVariable String usuarioId,
            @PathVariable String setorId,
            @RequestBody SetorPlantaModel plantaSetor
    ) {
        SetorPlantaModel novaPlantaSetor = usuarioService.adicionarPlantaNoSetor(usuarioId, setorId, plantaSetor);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaPlantaSetor);
    }

    @DeleteMapping("/{usuarioId}/setores/{setorId}/plantas/{plantaSetorId}")
    public ResponseEntity<Void> removerPlantaDoSetor(
            @PathVariable String usuarioId,
            @PathVariable String setorId,
            @PathVariable String plantaSetorId
    ) {
        usuarioService.removerPlantaDoSetor(usuarioId, setorId, plantaSetorId);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // ENDPOINTS DE TAREFAS
    // ==========================================

    @PostMapping("/{usuarioId}/setores/{setorId}/plantas/{plantaSetorId}/tarefas")
    public ResponseEntity<TarefaModel> adicionarTarefa(
            @PathVariable String usuarioId,
            @PathVariable String setorId,
            @PathVariable String plantaSetorId,
            @RequestBody TarefaModel tarefa
    ) {
        TarefaModel novaTarefa = usuarioService.adicionarTarefa(usuarioId, setorId, plantaSetorId, tarefa);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaTarefa);
    }

    @PatchMapping("/{usuarioId}/setores/{setorId}/plantas/{plantaSetorId}/tarefas/{tarefaId}/concluir")
    public ResponseEntity<TarefaModel> alternarConclusaoTarefa(
            @PathVariable String usuarioId,
            @PathVariable String setorId,
            @PathVariable String plantaSetorId,
            @PathVariable String tarefaId
    ) {
        TarefaModel tarefaAtualizada = usuarioService.alternarConclusaoTarefa(usuarioId, setorId, plantaSetorId, tarefaId);
        return ResponseEntity.ok(tarefaAtualizada);
    }

    @DeleteMapping("/{usuarioId}/setores/{setorId}/plantas/{plantaSetorId}/tarefas/{tarefaId}")
    public ResponseEntity<Void> removerTarefa(
            @PathVariable String usuarioId,
            @PathVariable String setorId,
            @PathVariable String plantaSetorId,
            @PathVariable String tarefaId
    ) {
        usuarioService.removerTarefa(usuarioId, setorId, plantaSetorId, tarefaId);
        return ResponseEntity.noContent().build();
    }
}