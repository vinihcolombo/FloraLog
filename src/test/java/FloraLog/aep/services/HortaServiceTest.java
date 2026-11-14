package FloraLog.aep.services;

import FloraLog.aep.models.HortaModel;
import FloraLog.aep.repositories.HortaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HortaServiceTest {

    @Mock
    private HortaRepository hortaRepository;

    @InjectMocks
    private HortaService hortaService;

    @Test
    void deveListarTodasAsHortas() {
        when(hortaRepository.findAll()).thenReturn(List.of(new HortaModel()));

        List<HortaModel> resultado = hortaService.listarTodas();

        assertEquals(1, resultado.size());
        verify(hortaRepository).findAll();
    }

    @Test
    void deveBuscarHortaPorId() {
        HortaModel horta = new HortaModel();
        when(hortaRepository.findById("1")).thenReturn(Optional.of(horta));

        HortaModel resultado = hortaService.buscarPorId("1");

        assertSame(horta, resultado);
    }

    @Test
    void deveLancarExcecaoQuandoHortaNaoExistir() {
        when(hortaRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> hortaService.buscarPorId("1"));
    }

    @Test
    void deveCadastrarHorta() {
        HortaModel horta = new HortaModel();
        horta.setNome("Horta Central");
        horta.setCapacidadeMaximaLotes(10);

        when(hortaRepository.save(any(HortaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        HortaModel resultado = hortaService.cadastrar(horta);

        assertNull(resultado.getId());
        assertEquals("Horta Central", resultado.getNome());
        verify(hortaRepository).save(horta);
    }

    @Test
    void naoDeveCadastrarHortaComNomeVazio() {
        HortaModel horta = new HortaModel();
        horta.setNome(" ");
        horta.setCapacidadeMaximaLotes(10);

        assertThrows(RuntimeException.class,
                () -> hortaService.cadastrar(horta));

        verify(hortaRepository, never()).save(any());
    }

    @Test
    void naoDeveCadastrarHortaComCapacidadeInvalida() {
        HortaModel horta = new HortaModel();
        horta.setNome("Horta Central");
        horta.setCapacidadeMaximaLotes(0);

        assertThrows(RuntimeException.class,
                () -> hortaService.cadastrar(horta));

        verify(hortaRepository, never()).save(any());
    }

    @Test
    void deveAtualizarHortaExistente() {
        HortaModel existente = new HortaModel();
        existente.setNome("Nome antigo");
        existente.setCapacidadeMaximaLotes(5);

        HortaModel dadosAtualizados = new HortaModel();
        dadosAtualizados.setNome("Nome novo");
        dadosAtualizados.setCapacidadeMaximaLotes(15);

        when(hortaRepository.findById("1")).thenReturn(Optional.of(existente));
        when(hortaRepository.save(any(HortaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        HortaModel resultado = hortaService.atualizar("1", dadosAtualizados);

        assertEquals("Nome novo", resultado.getNome());
        assertEquals(15, resultado.getCapacidadeMaximaLotes());
        verify(hortaRepository).save(existente);
    }

    @Test
    void deveExcluirHortaExistente() {
        HortaModel horta = new HortaModel();
        when(hortaRepository.findById("1")).thenReturn(Optional.of(horta));

        hortaService.excluir("1");

        verify(hortaRepository).delete(horta);
    }

    @Test
    void naoDeveCadastrarHortaComNomeNulo() {
        HortaModel horta = new HortaModel();
        horta.setCapacidadeMaximaLotes(10);

        assertThrows(RuntimeException.class,
                () -> hortaService.cadastrar(horta));

        verify(hortaRepository, never()).save(any());
    }
}