package FloraLog.aep.services;

import FloraLog.aep.models.LoteModel;
import FloraLog.aep.repositories.LoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoteService {

    private final LoteRepository loteRepository;

    public LoteService(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    public List<LoteModel> listarTodos() {
        return loteRepository.findAll();
    }

    public List<LoteModel> listarPorHorta(String hortaId) {
        return loteRepository.findByHortaId(hortaId);
    }

    public LoteModel buscarPorId(String id) {
        return loteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Lote não encontrado")
                );
    }

    public LoteModel cadastrar(LoteModel lote) {
        validarLote(lote);
        lote.setId(null);
        return loteRepository.save(lote);
    }

    public LoteModel atualizar(String id, LoteModel loteAtualizado) {
        validarLote(loteAtualizado);

        LoteModel loteExistente = buscarPorId(id);

        loteExistente.setNome(loteAtualizado.getNome());
        loteExistente.setHortaId(loteAtualizado.getHortaId());
        loteExistente.setTitularId(loteAtualizado.getTitularId());
        loteExistente.setColaboradoresIds(loteAtualizado.getColaboradoresIds());
        loteExistente.setPlantasIds(loteAtualizado.getPlantasIds());

        return hortaRepositorySaveGuard(loteExistente);
    }

    private LoteModel hortaRepositorySaveGuard(LoteModel lote) {
        return loteRepository.save(lote);
    }

    public void excluir(String id) {
        LoteModel lote = buscarPorId(id);
        loteRepository.delete(lote);
    }

    private void validarLote(LoteModel lote) {
        if (lote.getNome() == null || lote.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do lote é obrigatório");
        }

        if (lote.getHortaId() == null || lote.getHortaId().isBlank()) {
            throw new IllegalArgumentException("O ID da horta associada é obrigatório");
        }

        if (lote.getTitularId() == null || lote.getTitularId().isBlank()) {
            throw new IllegalArgumentException("O ID do titular principal do lote é obrigatório");
        }
    }
}