package FloraLog.aep.controllers;

import FloraLog.aep.models.SetorModel;
import FloraLog.aep.repositories.SetorRepository;
import FloraLog.aep.services.SetorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SetorControllerTest {

    private SetorService setorService;
    private SetorRepository setorRepository;
    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        setorService = mock(SetorService.class);
        setorRepository = mock(SetorRepository.class);

        SetorController controller = new SetorController();

        ReflectionTestUtils.setField(controller, "setorService", setorService);
        ReflectionTestUtils.setField(controller, "setorRepository", setorRepository);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void deveListarSetoresPorUsuario() throws Exception {
        when(setorService.listarPorUsuario("usuario1"))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/setores")
                        .param("usuarioId", "usuario1"))
                .andExpect(status().isOk());

        verify(setorService).listarPorUsuario("usuario1");
    }

    @Test
    void deveBuscarSetorExistente() throws Exception {
        when(setorRepository.findById("1"))
                .thenReturn(Optional.of(new SetorModel()));

        mockMvc.perform(get("/api/setores/1"))
                .andExpect(status().isOk());

        verify(setorRepository).findById("1");
    }

    @Test
    void deveRetornar404ParaSetorInexistente() throws Exception {
        when(setorRepository.findById("1"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/setores/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveSalvarSetor() throws Exception {
        when(setorService.salvar(any(SetorModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/setores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Jardim",
                                    "descricao": "Setor de plantas",
                                    "usuarioId": "usuario1"
                                }
                                """))
                .andExpect(status().isOk());

        verify(setorService).salvar(any(SetorModel.class));
    }
}