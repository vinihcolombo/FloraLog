package FloraLog.aep.services;

import FloraLog.aep.models.PlantaModel;
import FloraLog.aep.models.SetorModel;
import FloraLog.aep.models.SetorPlantaModel;
import FloraLog.aep.models.TarefaModel;
import FloraLog.aep.models.UsuarioModel;
import FloraLog.aep.models.UsuarioModel.PerfilUsuario;
import FloraLog.aep.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveListarTodosOsUsuarios() {
        when(usuarioRepository.findAll())
                .thenReturn(List.of(new UsuarioModel()));

        List<UsuarioModel> resultado = usuarioService.listarTodos();

        assertEquals(1, resultado.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void deveBuscarUsuarioPorId() {
        UsuarioModel usuario = new UsuarioModel();
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        assertSame(usuario, usuarioService.buscarPorId("1"));
    }

    @Test
    void deveLancarExcecaoAoBuscarUsuarioInexistente() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> usuarioService.buscarPorId("1"));
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        UsuarioModel usuario = new UsuarioModel();
        when(usuarioRepository.findByEmail("teste@email.com"))
                .thenReturn(Optional.of(usuario));

        assertSame(usuario,
                usuarioService.buscarPorEmail("teste@email.com"));
    }

    @Test
    void deveLancarExcecaoAoBuscarEmailInexistente() {
        when(usuarioRepository.findByEmail("inexistente@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> usuarioService.buscarPorEmail("inexistente@email.com"));
    }

    @Test
    void deveAutenticarUsuarioComSenhaCorreta() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setEmail("teste@email.com");
        usuario.setSenha("123456");

        when(usuarioRepository.findByEmail("teste@email.com"))
                .thenReturn(Optional.of(usuario));

        UsuarioModel resultado =
                usuarioService.autenticar("teste@email.com", "123456");

        assertSame(usuario, resultado);
    }

    @Test
    void deveRecusarAutenticacaoComSenhaIncorreta() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setEmail("teste@email.com");
        usuario.setSenha("123456");

        when(usuarioRepository.findByEmail("teste@email.com"))
                .thenReturn(Optional.of(usuario));

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.autenticar(
                        "teste@email.com", "senhaerrada"
                ));
    }

    @Test
    void deveCadastrarUsuario() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome("Teste");
        usuario.setEmail("teste@email.com");
        usuario.setSenha("123456");
        usuario.setPerfil(PerfilUsuario.DONO);

        when(usuarioRepository.save(any(UsuarioModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioModel resultado = usuarioService.cadastrar(usuario);

        assertNull(resultado.getId());
        assertEquals("Teste", resultado.getNome());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void naoDeveCadastrarUsuarioSemNome() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome(" ");
        usuario.setEmail("teste@email.com");
        usuario.setSenha("123456");
        usuario.setPerfil(PerfilUsuario.DONO);

        assertThrows(RuntimeException.class,
                () -> usuarioService.cadastrar(usuario));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoDeveCadastrarUsuarioSemEmail() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome("Teste");
        usuario.setEmail(" ");
        usuario.setSenha("123456");
        usuario.setPerfil(PerfilUsuario.DONO);

        assertThrows(RuntimeException.class,
                () -> usuarioService.cadastrar(usuario));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoDeveCadastrarUsuarioSemSenha() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome("Teste");
        usuario.setEmail("teste@email.com");
        usuario.setSenha(" ");
        usuario.setPerfil(PerfilUsuario.DONO);

        assertThrows(RuntimeException.class,
                () -> usuarioService.cadastrar(usuario));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoDeveCadastrarUsuarioSemPerfil() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNome("Teste");
        usuario.setEmail("teste@email.com");
        usuario.setSenha("123456");
        usuario.setPerfil(null);

        assertThrows(RuntimeException.class,
                () -> usuarioService.cadastrar(usuario));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveAtualizarUsuarioExistente() {
        UsuarioModel existente = new UsuarioModel();
        existente.setNome("Nome antigo");
        existente.setEmail("antigo@email.com");
        existente.setSenha("111111");

        UsuarioModel atualizado = new UsuarioModel();
        atualizado.setNome("Nome novo");
        atualizado.setEmail("novo@email.com");
        atualizado.setSenha("222222");
        atualizado.setPerfil(PerfilUsuario.GERENTE);

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(UsuarioModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioModel resultado = usuarioService.atualizar("1", atualizado);

        assertEquals("Nome novo", resultado.getNome());
        assertEquals("novo@email.com", resultado.getEmail());
        assertEquals("222222", resultado.getSenha());
        assertEquals(PerfilUsuario.GERENTE, resultado.getPerfil());

        verify(usuarioRepository).save(existente);
    }

    @Test
    void deveExcluirUsuarioExistente() {
        UsuarioModel usuario = new UsuarioModel();
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        usuarioService.excluir("1");

        verify(usuarioRepository).delete(usuario);
    }

    @Test
    void naoDeveCadastrarUsuarioComNomeNulo() {
        UsuarioModel usuario = new UsuarioModel(null, "teste@email.com", "123456", null, PerfilUsuario.DONO);

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cadastrar(usuario));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoDeveCadastrarUsuarioComEmailNulo() {
        UsuarioModel usuario = new UsuarioModel("Teste", null, "123456", null, PerfilUsuario.DONO);

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cadastrar(usuario));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoDeveCadastrarUsuarioComSenhaNula() {
        UsuarioModel usuario = new UsuarioModel("Teste", "teste@email.com", null, null, PerfilUsuario.DONO);

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cadastrar(usuario));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveListarBibliotecaDoUsuario() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.getBibliotecaPlantas().add(new PlantaModel());

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        List<PlantaModel> resultado = usuarioService.listarBiblioteca("1");

        assertEquals(1, resultado.size());
    }

    @Test
    void deveAdicionarPlantaNaBibliotecaGerandoCodigoQuandoNulo() {
        UsuarioModel usuario = new UsuarioModel();
        PlantaModel planta = new PlantaModel();
        planta.setCodigoPartilha(null);

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        PlantaModel resultado = usuarioService.adicionarPlantaBiblioteca("1", planta);

        assertNotNull(resultado.getCodigoPartilha());
        assertTrue(usuario.getBibliotecaPlantas().contains(planta));
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveAdicionarPlantaNaBibliotecaGerandoCodigoQuandoVazio() {
        UsuarioModel usuario = new UsuarioModel();
        PlantaModel planta = new PlantaModel();
        planta.setCodigoPartilha("");

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        PlantaModel resultado = usuarioService.adicionarPlantaBiblioteca("1", planta);

        assertEquals(8, resultado.getCodigoPartilha().length());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveAdicionarPlantaNaBibliotecaMantendoCodigoExistente() {
        UsuarioModel usuario = new UsuarioModel();
        PlantaModel planta = new PlantaModel();
        planta.setCodigoPartilha("ABC12345");

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        PlantaModel resultado = usuarioService.adicionarPlantaBiblioteca("1", planta);

        assertEquals("ABC12345", resultado.getCodigoPartilha());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveAtualizarPlantaDaBiblioteca() {
        UsuarioModel usuario = new UsuarioModel();
        PlantaModel existente = new PlantaModel();
        existente.setId("p1");
        existente.setFotoUrl("foto-antiga.png");
        usuario.getBibliotecaPlantas().add(existente);

        PlantaModel atualizada = new PlantaModel(
                "Manjericão", "Ocimum basilicum", "Erva", "Aromática",
                "Fácil", "Sol pleno", "Diária", "20-30°C", "foto-nova.png"
        );

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        PlantaModel resultado = usuarioService.atualizarPlantaBiblioteca("1", "p1", atualizada);

        assertEquals("Manjericão", resultado.getNome());
        assertEquals("Ocimum basilicum", resultado.getNomeCientifico());
        assertEquals("Erva", resultado.getCategoria());
        assertEquals("Aromática", resultado.getDescricao());
        assertEquals("Fácil", resultado.getNivelDificuldade());
        assertEquals("Sol pleno", resultado.getIluminacao());
        assertEquals("Diária", resultado.getRega());
        assertEquals("20-30°C", resultado.getTemperatura());
        assertEquals("foto-nova.png", resultado.getFotoUrl());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveManterFotoAoAtualizarPlantaSemFoto() {
        UsuarioModel usuario = new UsuarioModel();
        PlantaModel existente = new PlantaModel();
        existente.setId("p1");
        existente.setFotoUrl("foto-antiga.png");
        usuario.getBibliotecaPlantas().add(existente);

        PlantaModel atualizada = new PlantaModel();
        atualizada.setNome("Manjericão");

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        PlantaModel resultado = usuarioService.atualizarPlantaBiblioteca("1", "p1", atualizada);

        assertEquals("Manjericão", resultado.getNome());
        assertEquals("foto-antiga.png", resultado.getFotoUrl());
    }

    @Test
    void deveLancarExcecaoAoAtualizarPlantaInexistenteNaBiblioteca() {
        UsuarioModel usuario = new UsuarioModel();
        PlantaModel outra = new PlantaModel();
        outra.setId("p2");
        usuario.getBibliotecaPlantas().add(outra);

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        assertThrows(RuntimeException.class,
                () -> usuarioService.atualizarPlantaBiblioteca("1", "p1", new PlantaModel()));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveImportarPlantaPorCodigo() {
        UsuarioModel destino = new UsuarioModel();

        UsuarioModel semBiblioteca = new UsuarioModel();
        semBiblioteca.setBibliotecaPlantas(null);

        UsuarioModel semCodigo = new UsuarioModel();
        PlantaModel outraPlanta = new PlantaModel();
        outraPlanta.setCodigoPartilha("OUTRO123");
        semCodigo.getBibliotecaPlantas().add(outraPlanta);

        UsuarioModel origem = new UsuarioModel();
        PlantaModel original = new PlantaModel(
                "Manjericão", "Ocimum basilicum", "Erva", "Aromática",
                "Fácil", "Sol pleno", "Diária", "20-30°C", "foto.png"
        );
        original.setCodigoPartilha("ABC12345");
        origem.getBibliotecaPlantas().add(original);

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(destino));
        when(usuarioRepository.findAll())
                .thenReturn(List.of(semBiblioteca, semCodigo, origem));

        PlantaModel resultado = usuarioService.importarPlantaPorCodigo("1", "ABC12345");

        assertNotSame(original, resultado);
        assertEquals("Manjericão", resultado.getNome());
        assertEquals("Ocimum basilicum", resultado.getNomeCientifico());
        assertEquals("Erva", resultado.getCategoria());
        assertEquals("Aromática", resultado.getDescricao());
        assertEquals("Fácil", resultado.getNivelDificuldade());
        assertEquals("Sol pleno", resultado.getIluminacao());
        assertEquals("Diária", resultado.getRega());
        assertEquals("20-30°C", resultado.getTemperatura());
        assertEquals("foto.png", resultado.getFotoUrl());
        assertNotEquals("ABC12345", resultado.getCodigoPartilha());
        assertTrue(destino.getBibliotecaPlantas().contains(resultado));
        verify(usuarioRepository).save(destino);
    }

    @Test
    void deveLancarExcecaoAoImportarCodigoInexistente() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(new UsuarioModel()));
        when(usuarioRepository.findAll())
                .thenReturn(List.of(new UsuarioModel()));

        assertThrows(RuntimeException.class,
                () -> usuarioService.importarPlantaPorCodigo("1", "INEXISTENTE"));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveRemoverPlantaDaBiblioteca() {
        UsuarioModel usuario = new UsuarioModel();
        PlantaModel planta = new PlantaModel();
        planta.setId("p1");
        usuario.getBibliotecaPlantas().add(planta);

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        usuarioService.removerPlantaBiblioteca("1", "p1");

        assertTrue(usuario.getBibliotecaPlantas().isEmpty());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoAoRemoverPlantaInexistenteDaBiblioteca() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(new UsuarioModel()));

        assertThrows(RuntimeException.class,
                () -> usuarioService.removerPlantaBiblioteca("1", "p1"));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveListarSetoresDoUsuario() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setSetores(new ArrayList<>(List.of(new SetorModel())));

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        List<SetorModel> resultado = usuarioService.listarSetores("1");

        assertEquals(1, resultado.size());
    }

    @Test
    void deveAdicionarSetor() {
        UsuarioModel usuario = new UsuarioModel();
        SetorModel setor = new SetorModel("Varanda", "Plantas de sol");

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        SetorModel resultado = usuarioService.adicionarSetor("1", setor);

        assertEquals("Varanda", resultado.getNome());
        assertEquals("Plantas de sol", resultado.getDescricao());
        assertTrue(usuario.getSetores().contains(setor));
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveRemoverSetor() {
        UsuarioModel usuario = criarUsuarioComTarefa();

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        usuarioService.removerSetor("1", "s1");

        assertTrue(usuario.getSetores().isEmpty());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoAoRemoverSetorInexistente() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(criarUsuarioComTarefa()));

        assertThrows(RuntimeException.class,
                () -> usuarioService.removerSetor("1", "inexistente"));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveAdicionarPlantaNoSetor() {
        UsuarioModel usuario = criarUsuarioComTarefa();
        SetorPlantaModel plantaSetor = new SetorPlantaModel(
                "p1", "Manjericão", "Ocimum basilicum", "foto.png", "Sol pleno", "Diária", "20-30°C"
        );

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        SetorPlantaModel resultado = usuarioService.adicionarPlantaNoSetor("1", "s1", plantaSetor);

        assertSame(plantaSetor, resultado);
        assertEquals("p1", resultado.getPlantaBibliotecaId());
        assertEquals("Manjericão", resultado.getNome());
        assertEquals("Ocimum basilicum", resultado.getNomeCientifico());
        assertEquals("foto.png", resultado.getFotoUrl());
        assertEquals("Sol pleno", resultado.getIluminacao());
        assertEquals("Diária", resultado.getRega());
        assertEquals("20-30°C", resultado.getTemperatura());
        assertEquals(2, usuario.getSetores().get(0).getPlantas().size());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoAoAdicionarPlantaEmSetorInexistente() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(criarUsuarioComTarefa()));

        assertThrows(RuntimeException.class,
                () -> usuarioService.adicionarPlantaNoSetor("1", "inexistente", new SetorPlantaModel()));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveRemoverPlantaDoSetor() {
        UsuarioModel usuario = criarUsuarioComTarefa();

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        usuarioService.removerPlantaDoSetor("1", "s1", "sp1");

        assertTrue(usuario.getSetores().get(0).getPlantas().isEmpty());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoAoRemoverPlantaDeSetorInexistente() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(criarUsuarioComTarefa()));

        assertThrows(RuntimeException.class,
                () -> usuarioService.removerPlantaDoSetor("1", "inexistente", "sp1"));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoRemoverPlantaInexistenteDoSetor() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(criarUsuarioComTarefa()));

        assertThrows(RuntimeException.class,
                () -> usuarioService.removerPlantaDoSetor("1", "s1", "inexistente"));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveAdicionarTarefa() {
        UsuarioModel usuario = criarUsuarioComTarefa();
        TarefaModel tarefa = new TarefaModel("Adubar", "ADUBACAO", "MENSAL", LocalDate.of(2026, 12, 1));

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        TarefaModel resultado = usuarioService.adicionarTarefa("1", "s1", "sp1", tarefa);

        assertEquals("Adubar", resultado.getDescricao());
        assertEquals("ADUBACAO", resultado.getTipo());
        assertEquals("MENSAL", resultado.getFrequencia());
        assertEquals(LocalDate.of(2026, 12, 1), resultado.getDataVencimento());
        assertFalse(resultado.isConcluida());
        assertEquals(2, usuario.getSetores().get(0).getPlantas().get(0).getTarefas().size());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoAoAdicionarTarefaEmSetorInexistente() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(criarUsuarioComTarefa()));

        assertThrows(RuntimeException.class,
                () -> usuarioService.adicionarTarefa("1", "inexistente", "sp1", new TarefaModel()));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAdicionarTarefaEmPlantaInexistente() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(criarUsuarioComTarefa()));

        assertThrows(RuntimeException.class,
                () -> usuarioService.adicionarTarefa("1", "s1", "inexistente", new TarefaModel()));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveConcluirTarefaPendente() {
        UsuarioModel usuario = criarUsuarioComTarefa();

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        TarefaModel resultado = usuarioService.alternarConclusaoTarefa("1", "s1", "sp1", "t1");

        assertTrue(resultado.isConcluida());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveReabrirTarefaConcluida() {
        UsuarioModel usuario = criarUsuarioComTarefa();
        usuario.getSetores().get(0).getPlantas().get(0).getTarefas().get(0).setConcluida(true);

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        TarefaModel resultado = usuarioService.alternarConclusaoTarefa("1", "s1", "sp1", "t1");

        assertFalse(resultado.isConcluida());
    }

    @Test
    void deveLancarExcecaoAoAlternarTarefaEmSetorInexistente() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(criarUsuarioComTarefa()));

        assertThrows(RuntimeException.class,
                () -> usuarioService.alternarConclusaoTarefa("1", "inexistente", "sp1", "t1"));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAlternarTarefaEmPlantaInexistente() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(criarUsuarioComTarefa()));

        assertThrows(RuntimeException.class,
                () -> usuarioService.alternarConclusaoTarefa("1", "s1", "inexistente", "t1"));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAlternarTarefaInexistente() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(criarUsuarioComTarefa()));

        assertThrows(RuntimeException.class,
                () -> usuarioService.alternarConclusaoTarefa("1", "s1", "sp1", "inexistente"));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveRemoverTarefa() {
        UsuarioModel usuario = criarUsuarioComTarefa();

        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(usuario));

        usuarioService.removerTarefa("1", "s1", "sp1", "t1");

        assertTrue(usuario.getSetores().get(0).getPlantas().get(0).getTarefas().isEmpty());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoAoRemoverTarefaEmSetorInexistente() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(criarUsuarioComTarefa()));

        assertThrows(RuntimeException.class,
                () -> usuarioService.removerTarefa("1", "inexistente", "sp1", "t1"));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoRemoverTarefaEmPlantaInexistente() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(criarUsuarioComTarefa()));

        assertThrows(RuntimeException.class,
                () -> usuarioService.removerTarefa("1", "s1", "inexistente", "t1"));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoRemoverTarefaInexistente() {
        when(usuarioRepository.findById("1"))
                .thenReturn(Optional.of(criarUsuarioComTarefa()));

        assertThrows(RuntimeException.class,
                () -> usuarioService.removerTarefa("1", "s1", "sp1", "inexistente"));

        verify(usuarioRepository, never()).save(any());
    }

    private UsuarioModel criarUsuarioComTarefa() {
        TarefaModel tarefa = new TarefaModel();
        tarefa.setId("t1");
        tarefa.setDescricao("Regar");
        tarefa.setTipo("REGA");
        tarefa.setFrequencia("DIARIA");
        tarefa.setDataVencimento(LocalDate.of(2026, 12, 1));

        SetorPlantaModel plantaSetor = new SetorPlantaModel();
        plantaSetor.setId("sp1");
        plantaSetor.setPlantaBibliotecaId("p1");
        plantaSetor.setNome("Manjericão");
        plantaSetor.setNomeCientifico("Ocimum basilicum");
        plantaSetor.setFotoUrl("foto.png");
        plantaSetor.setIluminacao("Sol pleno");
        plantaSetor.setRega("Diária");
        plantaSetor.setTemperatura("20-30°C");
        plantaSetor.setTarefas(new ArrayList<>(List.of(tarefa)));

        SetorModel setor = new SetorModel();
        setor.setId("s1");
        setor.setNome("Varanda");
        setor.setDescricao("Plantas de sol");
        setor.setPlantas(new ArrayList<>(List.of(plantaSetor)));

        UsuarioModel usuario = new UsuarioModel();
        usuario.setSetores(new ArrayList<>(List.of(setor)));
        return usuario;
    }
}