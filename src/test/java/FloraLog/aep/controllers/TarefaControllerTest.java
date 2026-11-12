package FloraLog.aep.controllers;

import FloraLog.aep.models.TarefaModel;
import FloraLog.aep.services.TarefaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TarefaControllerTest {

    private TarefaService tarefaService;
    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        tarefaService = mock(TarefaService.class);

        TarefaController controller = new TarefaController();

        ReflectionTestUtils.setField(controller, "tarefaService", tarefaService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void deveListarTarefasPorSetorPlanta() throws Exception {
        when(tarefaService.listarPorInstancia("instancia1"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/tarefas")
                        .param("setorPlantaId", "instancia1"))
                .andExpect(status().isOk());

        verify(tarefaService).listarPorInstancia("instancia1");
    }

    @Test
    void deveCadastrarTarefa() throws Exception {
        when(tarefaService.salvar(any(TarefaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "descricao": "Regar plantas",
                                    "setorPlantaId": "instancia1",
                                    "usuarioId": "usuario1",
                                    "concluida": false
                                }
                                """))
                .andExpect(status().isCreated());

        verify(tarefaService).salvar(any(TarefaModel.class));
    }

    @Test
    void deveAlternarConclusaoDaTarefa() throws Exception {
        when(tarefaService.alternarConclusao("1"))
                .thenReturn(new TarefaModel());

        mockMvc.perform(patch("/api/tarefas/1/concluir"))
                .andExpect(status().isOk());

        verify(tarefaService).alternarConclusao("1");
    }

    @Test
    void deveExcluirTarefa() throws Exception {
        mockMvc.perform(delete("/api/tarefas/1"))
                .andExpect(status().isNoContent());

        verify(tarefaService).deletar("1");
    }
}