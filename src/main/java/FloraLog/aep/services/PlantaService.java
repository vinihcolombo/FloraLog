package FloraLog.aep.services;

import FloraLog.aep.models.PlantaModel;
import FloraLog.aep.repositories.PlantaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlantaService {

    @Autowired
    private PlantaRepository plantaRepository;

    public List<PlantaModel> listarBibliotecaPorUsuario(String usuarioId) {
        return plantaRepository.findByUsuarioId(usuarioId);
    }

    public PlantaModel salvar(PlantaModel planta) {
        if (planta.getCodigoPartilha() == null || planta.getCodigoPartilha().isEmpty()) {
            planta.setCodigoPartilha(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        return plantaRepository.save(planta);
    }

    public PlantaModel atualizar(String id, PlantaModel plantaAtualizada) {
        Optional<PlantaModel> plantaExistente = plantaRepository.findById(id);

        if (plantaExistente.isPresent()) {
            PlantaModel planta = plantaExistente.get();
            planta.setNome(plantaAtualizada.getNome());
            planta.setNomeCientifico(plantaAtualizada.getNomeCientifico());
            planta.setCategoria(plantaAtualizada.getCategoria());
            planta.setDescricao(plantaAtualizada.getDescricao());
            planta.setNivelDificuldade(plantaAtualizada.getNivelDificuldade());
            planta.setIluminacao(plantaAtualizada.getIluminacao());
            planta.setRega(plantaAtualizada.getRega());
            planta.setTemperatura(plantaAtualizada.getTemperatura());

            if (plantaAtualizada.getFotoUrl() != null) {
                planta.setFotoUrl(plantaAtualizada.getFotoUrl());
            }

            return plantaRepository.save(planta);
        }

        throw new RuntimeException("Planta não encontrada com o ID: " + id);
    }

    public void deletar(String id) {
        if (plantaRepository.existsById(id)) {
            plantaRepository.deleteById(id);
        } else {
            throw new RuntimeException("Planta não encontrada para exclusão, ID: " + id);
        }
    }

    public PlantaModel buscarPorId(String id) {
        return plantaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planta não encontrada com o ID: " + id));
    }

    public PlantaModel importarPorCodigo(String codigo, String usuarioId) {
        PlantaModel plantaOriginal = plantaRepository.findByCodigoPartilha(codigo)
                .orElseThrow(() -> new RuntimeException("Espécie não encontrada com este código."));

        PlantaModel novaPlanta = new PlantaModel();
        novaPlanta.setUsuarioId(usuarioId);
        novaPlanta.setNome(plantaOriginal.getNome());
        novaPlanta.setNomeCientifico(plantaOriginal.getNomeCientifico());
        novaPlanta.setCategoria(plantaOriginal.getCategoria());
        novaPlanta.setNivelDificuldade(plantaOriginal.getNivelDificuldade());
        novaPlanta.setIluminacao(plantaOriginal.getIluminacao());
        novaPlanta.setRega(plantaOriginal.getRega());
        novaPlanta.setTemperatura(plantaOriginal.getTemperatura());
        novaPlanta.setDescricao(plantaOriginal.getDescricao());
        novaPlanta.setFotoUrl(plantaOriginal.getFotoUrl());
        novaPlanta.setCodigoPartilha(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return plantaRepository.save(novaPlanta);
    }
}