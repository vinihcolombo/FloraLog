package FloraLog.aep.services;

import FloraLog.aep.models.UsuarioModel;
import FloraLog.aep.models.UsuarioModel.PerfilUsuario;
import FloraLog.aep.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveListarTodosOsUsuarios() {
        when(usuarioRepository.findAll())
                .thenReturn(List.of(new UsuarioModel()));

        List<UsuarioModel> resultado = usuarioService.listarTodos();

        assertEquals(1, resultado.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void deveBuscarUsuarioPorId() {
        UsuarioModel usuario = new UsuarioModel();
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        assertSame(usuario, usuarioService.buscarPorId("1"));
    }

    @Test
    void deveLancarExcecaoAoBuscarUsuarioInexistente() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> usuarioService.buscarPorId("1"));
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        UsuarioModel usuario = new UsuarioModel();
        when(usuarioRepository.findByEmail("teste@email.com"))
                .thenReturn(Optional.of(usuario));

        assertSame(usuario,
                usuarioService.buscarPorEmail("teste@email.com"));
    }

    @Test
    void deveLancarExcecaoAoBuscarEmailInexistente() {
        when(usuarioRepository.findByEmail("inexistente@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> usuarioService.buscarPorEmail("inexistente@email.com"));
    }

    @Test
    void deveAutenticarUsuarioComSenhaCorreta() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setEmail("teste@email.com");
        usuario.setSenha("123456");

        when(usuarioRepository.findByEmail("teste@email.com"))
                .thenReturn(Optional.of(usuario));

        UsuarioModel resultado =
                usuarioService.autenticar("teste@email.com", "123456");

        assertSame(usuario, resultado);
    }

    @Test
    void deveRecusarAutenticacaoComSenhaIncorreta() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setEmail("teste@email.com");
        usuario.setSenha("123456");

        when(usuarioRepository.findByEmail("teste@email.com"))
                .thenReturn(Optional.of(usuario));

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.autenticar(
                        "teste@email.com", "senhaerrada"
                ));
    }

    @Test
    void deveCadastrarUsuario() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome("Teste");
        usuario.setEmail("teste@email.com");
        usuario.setSenha("123456");
        usuario.setPerfil(PerfilUsuario.DONO);

        when(usuarioRepository.save(any(UsuarioModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioModel resultado = usuarioService.cadastrar(usuario);

        assertNull(resultado.getId());
        assertEquals("Teste", resultado.getNome());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void naoDeveCadastrarUsuarioSemNome() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome(" ");
        usuario.setEmail("teste@email.com");
        usuario.setSenha("123456");
        usuario.setPerfil(PerfilUsuario.DONO);

        assertThrows(RuntimeException.class,
                () -> usuarioService.cadastrar(usuario));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoDeveCadastrarUsuarioSemEmail() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome("Teste");
        usuario.setEmail(" ");
        usuario.setSenha("123456");
        usuario.setPerfil(PerfilUsuario.DONO);

        assertThrows(RuntimeException.class,
                () -> usuarioService.cadastrar(usuario));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoDeveCadastrarUsuarioSemSenha() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome("Teste");
        usuario.setEmail("teste@email.com");
        usuario.setSenha(" ");
        usuario.setPerfil(PerfilUsuario.DONO);

        assertThrows(RuntimeException.class,
                () -> usuarioService.cadastrar(usuario));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoDeveCadastrarUsuarioSemPerfil() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome("Teste");
        usuario.setEmail("teste@email.com");
        usuario.setSenha("123456");
        usuario.setPerfil(null);

        assertThrows(RuntimeException.class,
                () -> usuarioService.cadastrar(usuario));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveAtualizarUsuarioExistente() {
        UsuarioModel existente = new UsuarioModel();
        existente.setNome("Nome antigo");
        existente.setEmail("antigo@email.com");
        existente.setSenha("111111");

        UsuarioModel atualizado = new UsuarioModel();
        atualizado.setNome("Nome novo");
        atualizado.setEmail("novo@email.com");
        atualizado.setSenha("222222");
        atualizado.setPerfil(PerfilUsuario.GERENTE);

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(UsuarioModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioModel resultado = usuarioService.atualizar("1", atualizado);

        assertEquals("Nome novo", resultado.getNome());
        assertEquals("novo@email.com", resultado.getEmail());
        assertEquals("222222", resultado.getSenha());
        assertEquals(PerfilUsuario.GERENTE, resultado.getPerfil());

        verify(usuarioRepository).save(existente);
    }

    @Test
    void deveExcluirUsuarioExistente() {
        UsuarioModel usuario = new UsuarioModel();
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        usuarioService.excluir("1");

        verify(usuarioRepository).delete(usuario);
    }
}