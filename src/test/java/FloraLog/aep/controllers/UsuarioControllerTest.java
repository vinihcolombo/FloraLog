package FloraLog.aep.controllers;

import FloraLog.aep.models.UsuarioModel;
import FloraLog.aep.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UsuarioControllerTest {

    private UsuarioService usuarioService;
    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        usuarioService = mock(UsuarioService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new UsuarioController(usuarioService))
                .build();
    }

    @Test
    void deveListarUsuarios() throws Exception {
        when(usuarioService.listarTodos())
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk());

        verify(usuarioService).listarTodos();
    }

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        when(usuarioService.buscarPorId("1"))
                .thenReturn(new UsuarioModel());

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk());

        verify(usuarioService).buscarPorId("1");
    }

    @Test
    void deveBuscarUsuarioPorEmail() throws Exception {
        when(usuarioService.buscarPorEmail("teste@email.com"))
                .thenReturn(new UsuarioModel());

        mockMvc.perform(get("/api/usuarios/email/teste@email.com"))
                .andExpect(status().isOk());

        verify(usuarioService).buscarPorEmail("teste@email.com");
    }

    @Test
    void deveAutenticarUsuario() throws Exception {
        when(usuarioService.autenticar("teste@email.com", "123456"))
                .thenReturn(new UsuarioModel());

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "teste@email.com",
                                    "senha": "123456"
                                }
                                """))
                .andExpect(status().isOk());

        verify(usuarioService).autenticar("teste@email.com", "123456");
    }

    @Test
    void deveCadastrarUsuario() throws Exception {
        when(usuarioService.cadastrar(any(UsuarioModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Usuario Teste",
                                    "email": "teste@email.com",
                                    "senha": "123456",
                                    "perfil": "DONO"
                                }
                                """))
                .andExpect(status().isCreated());

        verify(usuarioService).cadastrar(any(UsuarioModel.class));
    }

    @Test
    void deveAtualizarUsuario() throws Exception {
        when(usuarioService.atualizar(eq("1"), any(UsuarioModel.class)))
                .thenReturn(new UsuarioModel());

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Usuario Atualizado",
                                    "email": "novo@email.com",
                                    "senha": "654321",
                                    "perfil": "GERENTE"
                                }
                                """))
                .andExpect(status().isOk());

        verify(usuarioService).atualizar(eq("1"), any(UsuarioModel.class));
    }

    @Test
    void deveExcluirUsuario() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService).excluir("1");
    }
}