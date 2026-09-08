package FloraLog.aep.services;

import FloraLog.aep.models.PlantaModel;
import FloraLog.aep.repositories.PlantaRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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

    private PlantaModel planta;


    @BeforeEach
    void setup() {

        planta = new PlantaModel(
                "Espada-de-São-Jorge",
                "Dracaena trifasciata",
                "Sombra",
                "Planta resistente",
                "Fácil",
                "Luz Indireta",
                "1x por semana",
                "18°C a 30°C"
        );

        planta.setId("123");
    }


    @Test
    void deveListarTodasAsPlantas() {

        when(plantaRepository.findAll())
                .thenReturn(List.of(planta));

        List<PlantaModel> resultado =
                plantaService.listarTodas();

        assertEquals(1, resultado.size());

        assertEquals(
                "Espada-de-São-Jorge",
                resultado.get(0).getNome()
        );

        verify(plantaRepository)
                .findAll();
    }


    @Test
    void deveBuscarPlantaPorId() {

        when(plantaRepository.findById("123"))
                .thenReturn(Optional.of(planta));

        PlantaModel resultado =
                plantaService.buscarPorId("123");

        assertEquals("123", resultado.getId());

        verify(plantaRepository)
                .findById("123");
    }


    @Test
    void deveLancarExcecaoQuandoPlantaNaoExiste() {

        when(plantaRepository.findById("999"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> plantaService.buscarPorId("999")
        );
    }


    @Test
    void deveCadastrarPlanta() {

        when(plantaRepository.save(any(PlantaModel.class)))
                .thenReturn(planta);

        PlantaModel resultado =
                plantaService.cadastrar(planta);

        assertEquals(
                "Espada-de-São-Jorge",
                resultado.getNome()
        );

        verify(plantaRepository)
                .save(any(PlantaModel.class));
    }


    @Test
    void naoDeveCadastrarPlantaSemNome() {

        planta.setNome("");

        assertThrows(
                IllegalArgumentException.class,
                () -> plantaService.cadastrar(planta)
        );

        verify(plantaRepository, never())
                .save(any());
    }


    @Test
    void deveAtualizarPlanta() {

        PlantaModel novaPlanta = new PlantaModel(
                "Cacto",
                "Cactaceae",
                "Suculenta",
                "Planta do deserto",
                "Fácil",
                "Sol Pleno",
                "1x por semana",
                "20°C a 35°C"
        );

        when(plantaRepository.findById("123"))
                .thenReturn(Optional.of(planta));

        when(plantaRepository.save(any(PlantaModel.class)))
                .thenReturn(novaPlanta);

        PlantaModel resultado =
                plantaService.atualizar(
                        "123",
                        novaPlanta
                );

        assertEquals(
                "Cacto",
                resultado.getNome()
        );

        verify(plantaRepository)
                .save(any(PlantaModel.class));
    }


    @Test
    void deveExcluirPlanta() {

        when(plantaRepository.findById("123"))
                .thenReturn(Optional.of(planta));

        plantaService.excluir("123");

        verify(plantaRepository)
                .delete(planta);
    }
}