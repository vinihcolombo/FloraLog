package FloraLog.aep.services;

import FloraLog.aep.models.PlantaModel;
import FloraLog.aep.repositories.PlantaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantaService {

    private final PlantaRepository plantaRepository;

    public PlantaService(PlantaRepository plantaRepository) {
        this.plantaRepository = plantaRepository;
    }

    public List<PlantaModel> listarTodas() {
        return plantaRepository.findAll();
    }

    public PlantaModel buscarPorId(String id) {

        return plantaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Planta não encontrada")
                );
    }

    public PlantaModel cadastrar(PlantaModel planta) {

        validarPlanta(planta);

        planta.setId(null);

        return plantaRepository.save(planta);
    }

    public PlantaModel atualizar(String id, PlantaModel plantaAtualizada) {

        validarPlanta(plantaAtualizada);

        PlantaModel plantaExistente = buscarPorId(id);

        plantaExistente.setNome(plantaAtualizada.getNome());
        plantaExistente.setNomeCientifico(
                plantaAtualizada.getNomeCientifico()
        );
        plantaExistente.setCategoria(
                plantaAtualizada.getCategoria()
        );
        plantaExistente.setDescricao(
                plantaAtualizada.getDescricao()
        );
        plantaExistente.setNivelDificuldade(
                plantaAtualizada.getNivelDificuldade()
        );
        plantaExistente.setIluminacao(
                plantaAtualizada.getIluminacao()
        );
        plantaExistente.setRega(
                plantaAtualizada.getRega()
        );
        plantaExistente.setTemperatura(
                plantaAtualizada.getTemperatura()
        );

        return plantaRepository.save(plantaExistente);
    }

    public void excluir(String id) {

        PlantaModel planta = buscarPorId(id);

        plantaRepository.delete(planta);
    }

    private void validarPlanta(PlantaModel planta) {

        if (planta.getNome() == null ||
                planta.getNome().isBlank()) {

            throw new IllegalArgumentException(
                    "O nome da planta é obrigatório"
            );
        }

        if (planta.getNomeCientifico() == null ||
                planta.getNomeCientifico().isBlank()) {

            throw new IllegalArgumentException(
                    "O nome científico é obrigatório"
            );
        }

        if (planta.getCategoria() == null ||
                planta.getCategoria().isBlank()) {

            throw new IllegalArgumentException(
                    "A categoria é obrigatória"
            );
        }

        if (planta.getNivelDificuldade() == null ||
                planta.getNivelDificuldade().isBlank()) {

            throw new IllegalArgumentException(
                    "O nível de dificuldade é obrigatório"
            );
        }

        if (planta.getIluminacao() == null ||
                planta.getIluminacao().isBlank()) {

            throw new IllegalArgumentException(
                    "A iluminação é obrigatória"
            );
        }

        if (planta.getRega() == null ||
                planta.getRega().isBlank()) {

            throw new IllegalArgumentException(
                    "A rega é obrigatória"
            );
        }

        if (planta.getTemperatura() == null ||
                planta.getTemperatura().isBlank()) {

            throw new IllegalArgumentException(
                    "A temperatura é obrigatória"
            );
        }
    }
}