package FloraLog.aep.services;

import FloraLog.aep.models.SetorModel;
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
class SetorServiceTest {

    @Mock
    private SetorRepository setorRepository;

    @InjectMocks
    private SetorService setorService;

    @Test
    void deveListarSetoresPorUsuario() {
        when(setorRepository.findByUsuarioId("usuario1"))
                .thenReturn(List.of(new SetorModel()));

        List<SetorModel> resultado = setorService.listarPorUsuario("usuario1");

        assertEquals(1, resultado.size());
        verify(setorRepository).findByUsuarioId("usuario1");
    }

    @Test
    void deveSalvarSetor() {
        SetorModel setor = new SetorModel();
        setor.setNome("Jardim");

        when(setorRepository.save(any(SetorModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SetorModel resultado = setorService.salvar(setor);

        assertSame(setor, resultado);
        verify(setorRepository).save(setor);
    }
}