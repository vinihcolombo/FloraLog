package FloraLog.aep.services;

import FloraLog.aep.models.TarefaModel;
import FloraLog.aep.repositories.TarefaRepository;
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
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @InjectMocks
    private TarefaService tarefaService;

    @Test
    void deveListarTarefasPorInstancia() {
        when(tarefaRepository.findBySetorPlantaId("instancia1"))
                .thenReturn(List.of(new TarefaModel()));

        List<TarefaModel> resultado =
                tarefaService.listarPorInstancia("instancia1");

        assertEquals(1, resultado.size());
        verify(tarefaRepository).findBySetorPlantaId("instancia1");
    }

    @Test
    void deveSalvarTarefa() {
        TarefaModel tarefa = new TarefaModel();

        when(tarefaRepository.save(any(TarefaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TarefaModel resultado = tarefaService.salvar(tarefa);

        assertSame(tarefa, resultado);
        verify(tarefaRepository).save(tarefa);
    }

    @Test
    void deveConcluirTarefaPendente() {
        TarefaModel tarefa = new TarefaModel();
        tarefa.setConcluida(false);

        when(tarefaRepository.findById("1")).thenReturn(Optional.of(tarefa));
        when(tarefaRepository.save(any(TarefaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TarefaModel resultado = tarefaService.alternarConclusao("1");

        assertTrue(resultado.isConcluida());
        verify(tarefaRepository).save(tarefa);
    }

    @Test
    void deveReabrirTarefaConcluida() {
        TarefaModel tarefa = new TarefaModel();
        tarefa.setConcluida(true);

        when(tarefaRepository.findById("1")).thenReturn(Optional.of(tarefa));
        when(tarefaRepository.save(any(TarefaModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TarefaModel resultado = tarefaService.alternarConclusao("1");

        assertFalse(resultado.isConcluida());
        verify(tarefaRepository).save(tarefa);
    }

    @Test
    void deveLancarExcecaoAoAlternarTarefaInexistente() {
        when(tarefaRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> tarefaService.alternarConclusao("1"));

        verify(tarefaRepository, never()).save(any());
    }

    @Test
    void deveDeletarTarefa() {
        tarefaService.deletar("1");

        verify(tarefaRepository).deleteById("1");
    }
}