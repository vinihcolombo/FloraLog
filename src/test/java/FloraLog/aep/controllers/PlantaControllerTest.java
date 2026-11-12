package FloraLog.aep.controllers;

import FloraLog.aep.models.PlantaModel;
import FloraLog.aep.services.PlantaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PlantaControllerTest {

    private PlantaService plantaService;
    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        plantaService = mock(PlantaService.class);

        PlantaController controller = new PlantaController();

        ReflectionTestUtils.setField(controller, "plantaService", plantaService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void deveListarBibliotecaDoUsuario() throws Exception {
        when(plantaService.listarBibliotecaPorUsuario("usuario1"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/biblioteca/usuario/usuario1"))
                .andExpect(status().isOk());

        verify(plantaService).listarBibliotecaPorUsuario("usuario1");
    }

    @Test
    void deveBuscarPlantaPorId() throws Exception {
        when(plantaService.buscarPorId("1"))
                .thenReturn(new PlantaModel());

        mockMvc.perform(get("/api/biblioteca/1"))
                .andExpect(status().isOk());

        verify(plantaService).buscarPorId("1");
    }

    @Test
    void deveRetornar404AoBuscarPlantaInexistente() throws Exception {
        when(plantaService.buscarPorId("1"))
                .thenThrow(new RuntimeException("Planta não encontrada"));

        mockMvc.perform(get("/api/biblioteca/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveSalvarPlanta() throws Exception {
        when(plantaService.salvar(any(PlantaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/biblioteca")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Samambaia",
                                    "usuarioId": "usuario1"
                                }
                                """))
                .andExpect(status().isCreated());

        verify(plantaService).salvar(any(PlantaModel.class));
    }

    @Test
    void deveSalvarListaDePlantasEmLote() throws Exception {
        when(plantaService.salvar(any(PlantaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/biblioteca/lote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                    {"nome": "Samambaia"},
                                    {"nome": "Orquídea"}
                                ]
                                """))
                .andExpect(status().isCreated());

        verify(plantaService, times(2)).salvar(any(PlantaModel.class));
    }

    @Test
    void deveAtualizarPlanta() throws Exception {
        when(plantaService.atualizar(eq("1"), any(PlantaModel.class)))
                .thenReturn(new PlantaModel());

        mockMvc.perform(put("/api/biblioteca/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Planta Atualizada"
                                }
                                """))
                .andExpect(status().isOk());

        verify(plantaService).atualizar(eq("1"), any(PlantaModel.class));
    }

    @Test
    void deveRetornar404AoAtualizarPlantaInexistente() throws Exception {
        when(plantaService.atualizar(eq("1"), any(PlantaModel.class)))
                .thenThrow(new RuntimeException("Planta não encontrada"));

        mockMvc.perform(put("/api/biblioteca/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Atualizada"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveExcluirPlanta() throws Exception {
        mockMvc.perform(delete("/api/biblioteca/1"))
                .andExpect(status().isNoContent());

        verify(plantaService).deletar("1");
    }

    @Test
    void deveRetornar404AoExcluirPlantaInexistente() throws Exception {
        doThrow(new RuntimeException("Planta não encontrada"))
                .when(plantaService).deletar("1");

        mockMvc.perform(delete("/api/biblioteca/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveImportarPlantaPorCodigo() throws Exception {
        when(plantaService.importarPorCodigo("ABC12345", "usuario1"))
                .thenReturn(new PlantaModel());

        mockMvc.perform(post("/api/biblioteca/importar/ABC12345")
                        .param("usuarioId", "usuario1"))
                .andExpect(status().isCreated());

        verify(plantaService).importarPorCodigo("ABC12345", "usuario1");
    }

    @Test
    void deveRetornar404AoImportarCodigoInexistente() throws Exception {
        when(plantaService.importarPorCodigo("INVALIDO", "usuario1"))
                .thenThrow(new RuntimeException("Espécie não encontrada"));

        mockMvc.perform(post("/api/biblioteca/importar/INVALIDO")
                        .param("usuarioId", "usuario1"))
                .andExpect(status().isNotFound());
    }
}