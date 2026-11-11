package FloraLog.aep.services;

import FloraLog.aep.models.UsuarioModel;
import FloraLog.aep.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .orElseThrow(() ->
                        new RuntimeException("Utilizador não encontrado")
                );
    }

    public UsuarioModel buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Utilizador não encontrado com este email")
                );
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