package FloraLog.aep.services;

import FloraLog.aep.models.HortaModel;
import FloraLog.aep.repositories.HortaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HortaService {

    private final HortaRepository hortaRepository;

    public HortaService(HortaRepository hortaRepository) {
        this.hortaRepository = hortaRepository;
    }

    public List<HortaModel> listarTodas() {
        return hortaRepository.findAll();
    }

    public HortaModel buscarPorId(String id) {
        return hortaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Horta não encontrada")
                );
    }

    public HortaModel cadastrar(HortaModel horta) {
        validarHorta(horta);
        horta.setId(null);
        return hortaRepository.save(horta);
    }

    public HortaModel atualizar(String id, HortaModel hortaAtualizada) {
        validarHorta(hortaAtualizada);

        HortaModel hortaExistente = buscarPorId(id);

        hortaExistente.setNome(hortaAtualizada.getNome());
        hortaExistente.setCapacidadeMaximaLotes(hortaAtualizada.getCapacidadeMaximaLotes());
        hortaExistente.setDonosIds(hortaAtualizada.getDonosIds());
        hortaExistente.setGerentesIds(hortaAtualizada.getGerentesIds());

        return hortaRepository.save(hortaExistente);
    }

    public void excluir(String id) {
        HortaModel horta = buscarPorId(id);
        hortaRepository.delete(horta);
    }

    private void validarHorta(HortaModel horta) {
        if (horta.getNome() == null || horta.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da horta é obrigatório");
        }

        if (horta.getCapacidadeMaximaLotes() <= 0) {
            throw new IllegalArgumentException("A capacidade máxima de lotes deve ser maior que zero");
        }
    }
}