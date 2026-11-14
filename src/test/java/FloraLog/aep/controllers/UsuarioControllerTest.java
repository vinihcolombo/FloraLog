package FloraLog.aep.controllers;

import FloraLog.aep.models.PlantaModel;
import FloraLog.aep.models.SetorModel;
import FloraLog.aep.models.SetorPlantaModel;
import FloraLog.aep.models.TarefaModel;
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

    @Test
    void deveListarBiblioteca() throws Exception {
        when(usuarioService.listarBiblioteca("1"))
                .thenReturn(java.util.List.of(new PlantaModel()));

        mockMvc.perform(get("/api/usuarios/1/biblioteca"))
                .andExpect(status().isOk());

        verify(usuarioService).listarBiblioteca("1");
    }

    @Test
    void deveAdicionarPlantaNaBiblioteca() throws Exception {
        when(usuarioService.adicionarPlantaBiblioteca(eq("1"), any(PlantaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(1));

        mockMvc.perform(post("/api/usuarios/1/biblioteca")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "p1",
                                    "nome": "Manjericão",
                                    "nomeCientifico": "Ocimum basilicum",
                                    "categoria": "Erva",
                                    "descricao": "Aromática",
                                    "nivelDificuldade": "Fácil",
                                    "iluminacao": "Sol pleno",
                                    "rega": "Diária",
                                    "temperatura": "20-30°C",
                                    "fotoUrl": "foto.png",
                                    "codigoPartilha": "ABC12345"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigoPartilha").value("ABC12345"));

        verify(usuarioService).adicionarPlantaBiblioteca(eq("1"), any(PlantaModel.class));
    }

    @Test
    void deveAtualizarPlantaDaBiblioteca() throws Exception {
        when(usuarioService.atualizarPlantaBiblioteca(eq("1"), eq("p1"), any(PlantaModel.class)))
                .thenReturn(new PlantaModel());

        mockMvc.perform(put("/api/usuarios/1/biblioteca/p1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Manjericão"
                                }
                                """))
                .andExpect(status().isOk());

        verify(usuarioService).atualizarPlantaBiblioteca(eq("1"), eq("p1"), any(PlantaModel.class));
    }

    @Test
    void deveImportarPlantaPorCodigo() throws Exception {
        when(usuarioService.importarPlantaPorCodigo("1", "ABC12345"))
                .thenReturn(new PlantaModel());

        mockMvc.perform(post("/api/usuarios/1/biblioteca/importar/ABC12345"))
                .andExpect(status().isCreated());

        verify(usuarioService).importarPlantaPorCodigo("1", "ABC12345");
    }

    @Test
    void deveRetornarNotFoundAoImportarCodigoInexistente() throws Exception {
        when(usuarioService.importarPlantaPorCodigo("1", "INEXISTENTE"))
                .thenThrow(new RuntimeException("Espécie não encontrada com este código."));

        mockMvc.perform(post("/api/usuarios/1/biblioteca/importar/INEXISTENTE"))
                .andExpect(status().isNotFound());

        verify(usuarioService).importarPlantaPorCodigo("1", "INEXISTENTE");
    }

    @Test
    void deveRemoverPlantaDaBiblioteca() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1/biblioteca/p1"))
                .andExpect(status().isNoContent());

        verify(usuarioService).removerPlantaBiblioteca("1", "p1");
    }

    @Test
    void deveListarSetores() throws Exception {
        when(usuarioService.listarSetores("1"))
                .thenReturn(java.util.List.of(new SetorModel("Varanda", "Plantas de sol")));

        mockMvc.perform(get("/api/usuarios/1/setores"))
                .andExpect(status().isOk());

        verify(usuarioService).listarSetores("1");
    }

    @Test
    void deveAdicionarSetor() throws Exception {
        when(usuarioService.adicionarSetor(eq("1"), any(SetorModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(1));

        mockMvc.perform(post("/api/usuarios/1/setores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "s1",
                                    "nome": "Varanda",
                                    "descricao": "Plantas de sol",
                                    "plantas": []
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Varanda"));

        verify(usuarioService).adicionarSetor(eq("1"), any(SetorModel.class));
    }

    @Test
    void deveRemoverSetor() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1/setores/s1"))
                .andExpect(status().isNoContent());

        verify(usuarioService).removerSetor("1", "s1");
    }

    @Test
    void deveAdicionarPlantaNoSetor() throws Exception {
        when(usuarioService.adicionarPlantaNoSetor(eq("1"), eq("s1"), any(SetorPlantaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(2));

        mockMvc.perform(post("/api/usuarios/1/setores/s1/plantas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "sp1",
                                    "plantaBibliotecaId": "p1",
                                    "nome": "Manjericão",
                                    "nomeCientifico": "Ocimum basilicum",
                                    "fotoUrl": "foto.png",
                                    "iluminacao": "Sol pleno",
                                    "rega": "Diária",
                                    "temperatura": "20-30°C",
                                    "tarefas": []
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.plantaBibliotecaId").value("p1"));

        verify(usuarioService).adicionarPlantaNoSetor(eq("1"), eq("s1"), any(SetorPlantaModel.class));
    }

    @Test
    void deveRemoverPlantaDoSetor() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1/setores/s1/plantas/sp1"))
                .andExpect(status().isNoContent());

        verify(usuarioService).removerPlantaDoSetor("1", "s1", "sp1");
    }

    @Test
    void deveAdicionarTarefa() throws Exception {
        when(usuarioService.adicionarTarefa(eq("1"), eq("s1"), eq("sp1"), any(TarefaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(3));

        mockMvc.perform(post("/api/usuarios/1/setores/s1/plantas/sp1/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "t1",
                                    "descricao": "Regar",
                                    "tipo": "REGA",
                                    "frequencia": "DIARIA",
                                    "dataVencimento": "2026-12-01",
                                    "concluida": false
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Regar"));

        verify(usuarioService).adicionarTarefa(eq("1"), eq("s1"), eq("sp1"), any(TarefaModel.class));
    }

    @Test
    void deveAlternarConclusaoTarefa() throws Exception {
        when(usuarioService.alternarConclusaoTarefa("1", "s1", "sp1", "t1"))
                .thenReturn(new TarefaModel());

        mockMvc.perform(patch("/api/usuarios/1/setores/s1/plantas/sp1/tarefas/t1/concluir"))
                .andExpect(status().isOk());

        verify(usuarioService).alternarConclusaoTarefa("1", "s1", "sp1", "t1");
    }

    @Test
    void deveRemoverTarefa() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1/setores/s1/plantas/sp1/tarefas/t1"))
                .andExpect(status().isNoContent());

        verify(usuarioService).removerTarefa("1", "s1", "sp1", "t1");
    }
}