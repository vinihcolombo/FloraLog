package FloraLog.aep.services;

import FloraLog.aep.models.*;
import FloraLog.aep.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioModel> listarTodos() {
        return usuarioRepository.findAll();
    }

    public UsuarioModel buscarPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
    }

    public UsuarioModel buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado com este email"));
    }

    public UsuarioModel autenticar(String email, String senha) {
        UsuarioModel usuario = buscarPorEmail(email);

        if (!usuario.getSenha().equals(senha)) {
            throw new IllegalArgumentException("Senha incorreta");
        }

        return usuario;
    }

    public UsuarioModel cadastrar(UsuarioModel usuario) {
        validarUsuario(usuario);
        usuario.setId(null);
        return usuarioRepository.save(usuario);
    }

    public UsuarioModel atualizar(String id, UsuarioModel usuarioAtualizado) {
        validarUsuario(usuarioAtualizado);

        UsuarioModel usuarioExistente = buscarPorId(id);

        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        usuarioExistente.setSenha(usuarioAtualizado.getSenha());
        usuarioExistente.setAvatarUrl(usuarioAtualizado.getAvatarUrl());
        usuarioExistente.setPerfil(usuarioAtualizado.getPerfil());

        return usuarioRepository.save(usuarioExistente);
    }

    public void excluir(String id) {
        UsuarioModel usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    // ==========================================
    // GESTÃO DA BIBLIOTECA DE PLANTAS (Subdocumentos)
    // ==========================================

    public List<PlantaModel> listarBiblioteca(String usuarioId) {
        return buscarPorId(usuarioId).getBibliotecaPlantas();
    }

    public PlantaModel adicionarPlantaBiblioteca(String usuarioId, PlantaModel planta) {
        UsuarioModel usuario = buscarPorId(usuarioId);

        if (planta.getCodigoPartilha() == null || planta.getCodigoPartilha().isEmpty()) {
            planta.setCodigoPartilha(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        usuario.getBibliotecaPlantas().add(planta);
        usuarioRepository.save(usuario);
        return planta;
    }

    public PlantaModel atualizarPlantaBiblioteca(String usuarioId, String plantaId, PlantaModel plantaAtualizada) {
        UsuarioModel usuario = buscarPorId(usuarioId);

        PlantaModel planta = usuario.getBibliotecaPlantas().stream()
                .filter(p -> p.getId().equals(plantaId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Planta não encontrada na biblioteca."));

        planta.setNome(plantaAtualizada.getNome());
        planta.setNomeCientifico(plantaAtualizada.getNomeCientifico());
        planta.setCategoria(plantaAtualizada.getCategoria());
        planta.setNivelDificuldade(plantaAtualizada.getNivelDificuldade());
        planta.setIluminacao(plantaAtualizada.getIluminacao());
        planta.setRega(plantaAtualizada.getRega());
        planta.setTemperatura(plantaAtualizada.getTemperatura());
        planta.setDescricao(plantaAtualizada.getDescricao());
        if (plantaAtualizada.getFotoUrl() != null) {
            planta.setFotoUrl(plantaAtualizada.getFotoUrl());
        }

        usuarioRepository.save(usuario);
        return planta;
    }

    public PlantaModel importarPlantaPorCodigo(String usuarioId, String codigo) {
        UsuarioModel usuarioDestino = buscarPorId(usuarioId);

        List<UsuarioModel> todosUsuarios = usuarioRepository.findAll();
        PlantaModel plantaOriginal = null;

        for (UsuarioModel u : todosUsuarios) {
            if (u.getBibliotecaPlantas() != null) {
                var encontrada = u.getBibliotecaPlantas().stream()
                        .filter(p -> codigo.equals(p.getCodigoPartilha()))
                        .findFirst();
                if (encontrada.isPresent()) {
                    plantaOriginal = encontrada.get();
                    break;
                }
            }
        }

        if (plantaOriginal == null) {
            throw new RuntimeException("Espécie não encontrada com este código.");
        }

        PlantaModel novaPlanta = new PlantaModel();
        novaPlanta.setNome(plantaOriginal.getNome());
        novaPlanta.setNomeCientifico(plantaOriginal.getNomeCientifico());
        novaPlanta.setCategoria(plantaOriginal.getCategoria());
        novaPlanta.setNivelDificuldade(plantaOriginal.getNivelDificuldade());
        novaPlanta.setIluminacao(plantaOriginal.getIluminacao());
        novaPlanta.setRega(plantaOriginal.getRega());
        novaPlanta.setTemperatura(plantaOriginal.getTemperatura());
        novaPlanta.setDescricao(plantaOriginal.getDescricao());
        novaPlanta.setFotoUrl(plantaOriginal.getFotoUrl());
        novaPlanta.setCodigoPartilha(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        usuarioDestino.getBibliotecaPlantas().add(novaPlanta);
        usuarioRepository.save(usuarioDestino);
        return novaPlanta;
    }

    public void removerPlantaBiblioteca(String usuarioId, String plantaId) {
        UsuarioModel usuario = buscarPorId(usuarioId);
        boolean removido = usuario.getBibliotecaPlantas().removeIf(p -> p.getId().equals(plantaId));
        if (!removido) {
            throw new RuntimeException("Planta não encontrada na biblioteca.");
        }
        usuarioRepository.save(usuario);
    }

    // ==========================================
    // GESTÃO DE SETORES (Subdocumentos)
    // ==========================================

    public List<SetorModel> listarSetores(String usuarioId) {
        return buscarPorId(usuarioId).getSetores();
    }

    public SetorModel adicionarSetor(String usuarioId, SetorModel setor) {
        UsuarioModel usuario = buscarPorId(usuarioId);
        usuario.getSetores().add(setor);
        usuarioRepository.save(usuario);
        return setor;
    }

    public void removerSetor(String usuarioId, String setorId) {
        UsuarioModel usuario = buscarPorId(usuarioId);
        boolean removido = usuario.getSetores().removeIf(s -> s.getId().equals(setorId));
        if (!removido) {
            throw new RuntimeException("Setor não encontrado.");
        }
        usuarioRepository.save(usuario);
    }

    // ==========================================
    // GESTÃO DE PLANTAS NO SETOR (Subdocumentos aninhados)
    // ==========================================

    public SetorPlantaModel adicionarPlantaNoSetor(String usuarioId, String setorId, SetorPlantaModel plantaSetor) {
        UsuarioModel usuario = buscarPorId(usuarioId);

        SetorModel setor = usuario.getSetores().stream()
                .filter(s -> s.getId().equals(setorId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Setor não encontrado."));

        setor.getPlantas().add(plantaSetor);
        usuarioRepository.save(usuario);
        return plantaSetor;
    }

    public void removerPlantaDoSetor(String usuarioId, String setorId, String plantaSetorId) {
        UsuarioModel usuario = buscarPorId(usuarioId);

        SetorModel setor = usuario.getSetores().stream()
                .filter(s -> s.getId().equals(setorId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Setor não encontrado."));

        boolean removido = setor.getPlantas().removeIf(p -> p.getId().equals(plantaSetorId));
        if (!removido) {
            throw new RuntimeException("Planta do setor não encontrada.");
        }
        usuarioRepository.save(usuario);
    }

    // ==========================================
    // GESTÃO DE TAREFAS (Subdocumentos embutidos na planta do setor)
    // ==========================================

    public TarefaModel adicionarTarefa(String usuarioId, String setorId, String plantaSetorId, TarefaModel tarefa) {
        UsuarioModel usuario = buscarPorId(usuarioId);

        SetorModel setor = usuario.getSetores().stream()
                .filter(s -> s.getId().equals(setorId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Setor não encontrado."));

        SetorPlantaModel plantaSetor = setor.getPlantas().stream()
                .filter(p -> p.getId().equals(plantaSetorId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Planta do setor não encontrada."));

        plantaSetor.getTarefas().add(tarefa);
        usuarioRepository.save(usuario);
        return tarefa;
    }

    public TarefaModel alternarConclusaoTarefa(String usuarioId, String setorId, String plantaSetorId, String tarefaId) {
        UsuarioModel usuario = buscarPorId(usuarioId);

        SetorModel setor = usuario.getSetores().stream()
                .filter(s -> s.getId().equals(setorId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Setor não encontrado."));

        SetorPlantaModel plantaSetor = setor.getPlantas().stream()
                .filter(p -> p.getId().equals(plantaSetorId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Planta do setor não encontrada."));

        TarefaModel tarefa = plantaSetor.getTarefas().stream()
                .filter(t -> t.getId().equals(tarefaId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada."));

        tarefa.setConcluida(!tarefa.isConcluida());
        usuarioRepository.save(usuario);
        return tarefa;
    }

    public void removerTarefa(String usuarioId, String setorId, String plantaSetorId, String tarefaId) {
        UsuarioModel usuario = buscarPorId(usuarioId);

        SetorModel setor = usuario.getSetores().stream()
                .filter(s -> s.getId().equals(setorId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Setor não encontrado."));

        SetorPlantaModel plantaSetor = setor.getPlantas().stream()
                .filter(p -> p.getId().equals(plantaSetorId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Planta do setor não encontrada."));

        boolean removido = plantaSetor.getTarefas().removeIf(t -> t.getId().equals(tarefaId));
        if (!removido) {
            throw new RuntimeException("Tarefa não encontrada.");
        }
        usuarioRepository.save(usuario);
    }

    private void validarUsuario(UsuarioModel usuario) {
        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do utilizador é obrigatório");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("O email do utilizador é obrigatório");
        }

        if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            throw new IllegalArgumentException("A senha é obrigatória");
        }

        if (usuario.getPerfil() == null) {
            throw new IllegalArgumentException("O perfil de acesso é obrigatório");
        }
    }
}