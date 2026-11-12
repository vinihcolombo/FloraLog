package FloraLog.aep.controllers;

import FloraLog.aep.models.LoteModel;
import FloraLog.aep.services.LoteService;
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

class LoteControllerTest {

    private LoteService loteService;
    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        loteService = mock(LoteService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new LoteController(loteService))
                .build();
    }

    @Test
    void deveListarTodosOsLotes() throws Exception {
        when(loteService.listarTodos()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/lotes"))
                .andExpect(status().isOk());

        verify(loteService).listarTodos();
    }

    @Test
    void deveListarLotesPorHorta() throws Exception {
        when(loteService.listarPorHorta("horta1"))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/lotes/horta/horta1"))
                .andExpect(status().isOk());

        verify(loteService).listarPorHorta("horta1");
    }

    @Test
    void deveBuscarLotePorId() throws Exception {
        when(loteService.buscarPorId("1"))
                .thenReturn(new LoteModel());

        mockMvc.perform(get("/api/lotes/1"))
                .andExpect(status().isOk());

        verify(loteService).buscarPorId("1");
    }

    @Test
    void deveCadastrarLote() throws Exception {
        when(loteService.cadastrar(any(LoteModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/lotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Lote A",
                                    "hortaId": "horta1",
                                    "titularId": "usuario1"
                                }
                                """))
                .andExpect(status().isCreated());

        verify(loteService).cadastrar(any(LoteModel.class));
    }

    @Test
    void deveAtualizarLote() throws Exception {
        when(loteService.atualizar(eq("1"), any(LoteModel.class)))
                .thenReturn(new LoteModel());

        mockMvc.perform(put("/api/lotes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Lote Atualizado",
                                    "hortaId": "horta1",
                                    "titularId": "usuario1"
                                }
                                """))
                .andExpect(status().isOk());

        verify(loteService).atualizar(eq("1"), any(LoteModel.class));
    }

    @Test
    void deveExcluirLote() throws Exception {
        mockMvc.perform(delete("/api/lotes/1"))
                .andExpect(status().isNoContent());

        verify(loteService).excluir("1");
    }
}