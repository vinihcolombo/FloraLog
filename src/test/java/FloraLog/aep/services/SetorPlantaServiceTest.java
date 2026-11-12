package FloraLog.aep.services;

import FloraLog.aep.models.SetorPlantaModel;
import FloraLog.aep.repositories.SetorPlantaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetorPlantaServiceTest {

    @Mock
    private SetorPlantaRepository setorPlantaRepository;

    @InjectMocks
    private SetorPlantaService setorPlantaService;

    @Test
    void deveListarPlantasDoSetor() {
        when(setorPlantaRepository.findBySetorId("setor1"))
                .thenReturn(List.of(new SetorPlantaModel()));

        List<SetorPlantaModel> resultado =
                setorPlantaService.listarPlantasDoSetor("setor1");

        assertEquals(1, resultado.size());
        verify(setorPlantaRepository).findBySetorId("setor1");
    }

    @Test
    void deveVincularPlantaAoSetor() {
        when(setorPlantaRepository.save(any(SetorPlantaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SetorPlantaModel resultado =
                setorPlantaService.vincularPlantaAoSetor(
                        "setor1", "planta1", "usuario1"
                );

        assertEquals("setor1", resultado.getSetorId());
        assertEquals("planta1", resultado.getPlantaId());
        assertEquals("usuario1", resultado.getUsuarioId());

        verify(setorPlantaRepository).save(any(SetorPlantaModel.class));
    }

    @Test
    void deveRemoverVinculo() {
        setorPlantaService.removerVinculo("vinculo1");

        verify(setorPlantaRepository).deleteById("vinculo1");
    }
}