package FloraLog.aep.controllers;

import FloraLog.aep.models.SetorPlantaModel;
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

class SetorPlantaControllerTest {

    private SetorPlantaService setorPlantaService;
    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        setorPlantaService = mock(SetorPlantaService.class);

        SetorPlantaController controller = new SetorPlantaController();

        ReflectionTestUtils.setField(
                controller, "setorPlantaService", setorPlantaService
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void deveListarPlantasDoSetor() throws Exception {
        when(setorPlantaService.listarPlantasDoSetor("setor1"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/setor-plantas")
                        .param("setorId", "setor1"))
                .andExpect(status().isOk());

        verify(setorPlantaService).listarPlantasDoSetor("setor1");
    }

    @Test
    void deveVincularPlantaAoSetor() throws Exception {
        when(setorPlantaService.vincularPlantaAoSetor(
                any(), any(), any()
        )).thenReturn(new SetorPlantaModel());

        mockMvc.perform(post("/api/setor-plantas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "setorId": "setor1",
                                    "plantaId": "planta1",
                                    "usuarioId": "usuario1"
                                }
                                """))
                .andExpect(status().isOk());

        verify(setorPlantaService)
                .vincularPlantaAoSetor("setor1", "planta1", "usuario1");
    }

    @Test
    void deveRemoverVinculo() throws Exception {
        mockMvc.perform(delete("/api/setor-plantas/1"))
                .andExpect(status().isNoContent());

        verify(setorPlantaService).removerVinculo("1");
    }
}