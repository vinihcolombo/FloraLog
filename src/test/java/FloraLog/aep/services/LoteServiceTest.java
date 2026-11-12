package FloraLog.aep.services;

import FloraLog.aep.models.LoteModel;
import FloraLog.aep.repositories.LoteRepository;
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
class LoteServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private LoteService loteService;

    @Test
    void deveListarTodosOsLotes() {
        when(loteRepository.findAll()).thenReturn(List.of(new LoteModel()));

        List<LoteModel> resultado = loteService.listarTodos();

        assertEquals(1, resultado.size());
        verify(loteRepository).findAll();
    }

    @Test
    void deveListarLotesPorHorta() {
        when(loteRepository.findByHortaId("horta1"))
                .thenReturn(List.of(new LoteModel()));

        List<LoteModel> resultado = loteService.listarPorHorta("horta1");

        assertEquals(1, resultado.size());
        verify(loteRepository).findByHortaId("horta1");
    }

    @Test
    void deveBuscarLotePorId() {
        LoteModel lote = new LoteModel();
        when(loteRepository.findById("1")).thenReturn(Optional.of(lote));

        assertSame(lote, loteService.buscarPorId("1"));
    }

    @Test
    void deveLancarExcecaoQuandoLoteNaoExistir() {
        when(loteRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> loteService.buscarPorId("1"));
    }

    @Test
    void deveCadastrarLote() {
        LoteModel lote = new LoteModel();
        lote.setNome("Lote A");
        lote.setHortaId("horta1");
        lote.setTitularId("usuario1");

        when(loteRepository.save(any(LoteModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LoteModel resultado = loteService.cadastrar(lote);

        assertNull(resultado.getId());
        assertEquals("Lote A", resultado.getNome());
        verify(loteRepository).save(lote);
    }

    @Test
    void naoDeveCadastrarLoteComNomeVazio() {
        LoteModel lote = new LoteModel();
        lote.setNome(" ");
        lote.setHortaId("horta1");
        lote.setTitularId("usuario1");

        assertThrows(RuntimeException.class,
                () -> loteService.cadastrar(lote));

        verify(loteRepository, never()).save(any());
    }

    @Test
    void naoDeveCadastrarLoteSemHorta() {
        LoteModel lote = new LoteModel();
        lote.setNome("Lote A");
        lote.setHortaId(" ");
        lote.setTitularId("usuario1");

        assertThrows(RuntimeException.class,
                () -> loteService.cadastrar(lote));

        verify(loteRepository, never()).save(any());
    }

    @Test
    void naoDeveCadastrarLoteSemTitular() {
        LoteModel lote = new LoteModel();
        lote.setNome("Lote A");
        lote.setHortaId("horta1");
        lote.setTitularId(" ");

        assertThrows(RuntimeException.class,
                () -> loteService.cadastrar(lote));

        verify(loteRepository, never()).save(any());
    }

    @Test
    void deveAtualizarLoteExistente() {
        LoteModel existente = new LoteModel();
        existente.setNome("Antigo");
        existente.setHortaId("horta1");
        existente.setTitularId("usuario1");

        LoteModel atualizado = new LoteModel();
        atualizado.setNome("Novo");
        atualizado.setHortaId("horta2");
        atualizado.setTitularId("usuario2");

        when(loteRepository.findById("1")).thenReturn(Optional.of(existente));
        when(loteRepository.save(any(LoteModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LoteModel resultado = loteService.atualizar("1", atualizado);

        assertEquals("Novo", resultado.getNome());
        assertEquals("horta2", resultado.getHortaId());
        assertEquals("usuario2", resultado.getTitularId());
        verify(loteRepository).save(existente);
    }

    @Test
    void deveExcluirLoteExistente() {
        LoteModel lote = new LoteModel();
        when(loteRepository.findById("1")).thenReturn(Optional.of(lote));

        loteService.excluir("1");

        verify(loteRepository).delete(lote);
    }
}