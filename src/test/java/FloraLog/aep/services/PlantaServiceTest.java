package FloraLog.aep.services;

import FloraLog.aep.models.PlantaModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlantaServiceTest {

    @Mock
    private PlantaRepository plantaRepository;

    @InjectMocks
    private PlantaService plantaService;

    @Test
    void deveListarBibliotecaPorUsuario() {
        when(plantaRepository.findByUsuarioId("usuario1"))
                .thenReturn(List.of(new PlantaModel()));

        List<PlantaModel> resultado =
                plantaService.listarBibliotecaPorUsuario("usuario1");

        assertEquals(1, resultado.size());
        verify(plantaRepository).findByUsuarioId("usuario1");
    }

    @Test
    void deveSalvarPlantaGerandoCodigoQuandoNaoExistir() {
        PlantaModel planta = new PlantaModel();
        planta.setNome("Samambaia");
        planta.setCodigoPartilha(null);

        when(plantaRepository.save(any(PlantaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PlantaModel resultado = plantaService.salvar(planta);

        assertNotNull(resultado.getCodigoPartilha());
        assertEquals(8, resultado.getCodigoPartilha().length());
        verify(plantaRepository).save(planta);
    }

    @Test
    void deveManterCodigoDeCompartilhamentoExistente() {
        PlantaModel planta = new PlantaModel();
        planta.setCodigoPartilha("ABC12345");

        when(plantaRepository.save(any(PlantaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PlantaModel resultado = plantaService.salvar(planta);

        assertEquals("ABC12345", resultado.getCodigoPartilha());
        verify(plantaRepository).save(planta);
    }

    @Test
    void deveBuscarPlantaPorId() {
        PlantaModel planta = new PlantaModel();
        when(plantaRepository.findById("1")).thenReturn(Optional.of(planta));

        assertSame(planta, plantaService.buscarPorId("1"));
    }

    @Test
    void deveLancarExcecaoAoBuscarPlantaInexistente() {
        when(plantaRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> plantaService.buscarPorId("1"));
    }

    @Test
    void deveAtualizarPlantaExistente() {
        PlantaModel existente = new PlantaModel();
        existente.setNome("Nome antigo");
        existente.setFotoUrl("foto-antiga.jpg");

        PlantaModel atualizada = new PlantaModel();
        atualizada.setNome("Nome novo");
        atualizada.setFotoUrl("foto-nova.jpg");

        when(plantaRepository.findById("1")).thenReturn(Optional.of(existente));
        when(plantaRepository.save(any(PlantaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PlantaModel resultado = plantaService.atualizar("1", atualizada);

        assertEquals("Nome novo", resultado.getNome());
        assertEquals("foto-nova.jpg", resultado.getFotoUrl());
        verify(plantaRepository).save(existente);
    }

    @Test
    void deveManterFotoAntigaQuandoNovaFotoForNula() {
        PlantaModel existente = new PlantaModel();
        existente.setFotoUrl("foto-antiga.jpg");

        PlantaModel atualizada = new PlantaModel();
        atualizada.setNome("Nome novo");
        atualizada.setFotoUrl(null);

        when(plantaRepository.findById("1")).thenReturn(Optional.of(existente));
        when(plantaRepository.save(any(PlantaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PlantaModel resultado = plantaService.atualizar("1", atualizada);

        assertEquals("foto-antiga.jpg", resultado.getFotoUrl());
        verify(plantaRepository).save(existente);
    }

    @Test
    void deveLancarExcecaoAoAtualizarPlantaInexistente() {
        when(plantaRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> plantaService.atualizar("1", new PlantaModel()));

        verify(plantaRepository, never()).save(any());
    }

    @Test
    void deveExcluirPlantaExistente() {
        when(plantaRepository.existsById("1")).thenReturn(true);

        plantaService.deletar("1");

        verify(plantaRepository).deleteById("1");
    }

    @Test
    void deveLancarExcecaoAoExcluirPlantaInexistente() {
        when(plantaRepository.existsById("1")).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> plantaService.deletar("1"));

        verify(plantaRepository, never()).deleteById(any());
    }

    @Test
    void deveImportarPlantaPorCodigo() {
        PlantaModel original = new PlantaModel();
        original.setNome("Orquídea");
        original.setCodigoPartilha("ABC12345");

        when(plantaRepository.findByCodigoPartilha("ABC12345"))
                .thenReturn(Optional.of(original));

        when(plantaRepository.save(any(PlantaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PlantaModel resultado =
                plantaService.importarPorCodigo("ABC12345", "usuario2");

        assertEquals("Orquídea", resultado.getNome());
        assertEquals("usuario2", resultado.getUsuarioId());
        assertNotEquals("ABC12345", resultado.getCodigoPartilha());
        verify(plantaRepository).save(any(PlantaModel.class));
    }

    @Test
    void deveLancarExcecaoAoImportarCodigoInexistente() {
        when(plantaRepository.findByCodigoPartilha("INVALIDO"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> plantaService.importarPorCodigo("INVALIDO", "usuario2"));

        verify(plantaRepository, never()).save(any());
    }
}