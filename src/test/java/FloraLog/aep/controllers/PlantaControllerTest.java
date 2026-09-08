package FloraLog.aep.controllers;

import FloraLog.aep.models.PlantaModel;
import FloraLog.aep.services.PlantaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class PlantaControllerTest {

    @Mock
    private PlantaService plantaService;

    @InjectMocks
    private PlantaController plantaController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc criarMockMvc() {
        return MockMvcBuilders
                .standaloneSetup(plantaController)
                .build();
    }

    private PlantaModel criarPlanta() {

        PlantaModel planta = new PlantaModel(
                "Espada-de-São-Jorge",
                "Dracaena trifasciata",
                "Sombra",
                "Planta resistente e fácil de cuidar",
                "Fácil",
                "Luz Indireta",
                "1x por semana",
                "18°C a 30°C"
        );

        planta.setId("123");

        return planta;
    }


    @Test
    void deveListarTodasAsPlantas() throws Exception {

        PlantaModel planta = criarPlanta();

        when(plantaService.listarTodas())
                .thenReturn(List.of(planta));

        MockMvc mockMvc = criarMockMvc();

        mockMvc.perform(get("/api/plantas"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("123"))
                .andExpect(jsonPath("$[0].nome").value("Espada-de-São-Jorge"));

        verify(plantaService).listarTodas();
    }


    @Test
    void deveBuscarPlantaPorId() throws Exception {

        PlantaModel planta = criarPlanta();

        when(plantaService.buscarPorId("123"))
                .thenReturn(planta);

        MockMvc mockMvc = criarMockMvc();

        mockMvc.perform(get("/api/plantas/123"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.nome").value("Espada-de-São-Jorge"));

        verify(plantaService).buscarPorId("123");
    }


    @Test
    void deveCadastrarPlanta() throws Exception {

        PlantaModel planta = criarPlanta();

        when(plantaService.cadastrar(any(PlantaModel.class)))
                .thenReturn(planta);

        MockMvc mockMvc = criarMockMvc();

        mockMvc.perform(
                        post("/api/plantas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(planta))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.nome").value("Espada-de-São-Jorge"));

        verify(plantaService).cadastrar(any(PlantaModel.class));
    }


    @Test
    void deveAtualizarPlanta() throws Exception {

        PlantaModel planta = criarPlanta();

        when(plantaService.atualizar(
                eq("123"),
                any(PlantaModel.class)
        )).thenReturn(planta);

        MockMvc mockMvc = criarMockMvc();

        mockMvc.perform(
                        put("/api/plantas/123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(planta))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.nome").value("Espada-de-São-Jorge"));

        verify(plantaService).atualizar(
                eq("123"),
                any(PlantaModel.class)
        );
    }


    @Test
    void deveExcluirPlanta() throws Exception {

        doNothing().when(plantaService).excluir("123");

        MockMvc mockMvc = criarMockMvc();

        mockMvc.perform(delete("/api/plantas/123"))
                .andExpect(status().isNoContent());

        verify(plantaService).excluir("123");
    }
}