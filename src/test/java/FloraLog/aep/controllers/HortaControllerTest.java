package FloraLog.aep.controllers;

import FloraLog.aep.models.HortaModel;
import FloraLog.aep.services.HortaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HortaControllerTest {

    private HortaService hortaService;
    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        hortaService = mock(HortaService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new HortaController(hortaService))
                .build();
    }

    @Test
    void deveListarTodasAsHortas() throws Exception {
        when(hortaService.listarTodas()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/hortas"))
                .andExpect(status().isOk());

        verify(hortaService).listarTodas();
    }

    @Test
    void deveBuscarHortaPorId() throws Exception {
        when(hortaService.buscarPorId("1"))
                .thenReturn(new HortaModel());

        mockMvc.perform(get("/api/hortas/1"))
                .andExpect(status().isOk());

        verify(hortaService).buscarPorId("1");
    }

    @Test
    void deveCadastrarHorta() throws Exception {
        when(hortaService.cadastrar(any(HortaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/hortas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Horta Central",
                                    "capacidadeMaximaLotes": 10
                                }
                                """))
                .andExpect(status().isCreated());

        verify(hortaService).cadastrar(any(HortaModel.class));
    }

    @Test
    void deveAtualizarHorta() throws Exception {
        when(hortaService.atualizar(eq("1"), any(HortaModel.class)))
                .thenReturn(new HortaModel());

        mockMvc.perform(put("/api/hortas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Horta Atualizada",
                                    "capacidadeMaximaLotes": 15
                                }
                                """))
                .andExpect(status().isOk());

        verify(hortaService).atualizar(eq("1"), any(HortaModel.class));
    }

    @Test
    void deveExcluirHorta() throws Exception {
        doNothing().when(hortaService).excluir("1");

        mockMvc.perform(delete("/api/hortas/1"))
                .andExpect(status().isNoContent());

        verify(hortaService).excluir("1");
    }
}