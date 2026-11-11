package FloraLog.aep.services;

import FloraLog.aep.models.SetorPlantaModel;
import FloraLog.aep.repositories.SetorPlantaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetorPlantaService {

    @Autowired
    private SetorPlantaRepository setorPlantaRepository;

    public List<SetorPlantaModel> listarPlantasDoSetor(String setorId) {
        return setorPlantaRepository.findBySetorId(setorId);
    }

    public SetorPlantaModel vincularPlantaAoSetor(String setorId, String plantaId, String usuarioId) {
        SetorPlantaModel associacao = new SetorPlantaModel(setorId, plantaId, usuarioId);
        return setorPlantaRepository.save(associacao);
    }

    public void removerVinculo(String id) {
        setorPlantaRepository.deleteById(id);
    }
}