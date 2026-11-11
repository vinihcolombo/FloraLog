package FloraLog.aep.services;

import FloraLog.aep.models.SetorModel;
import FloraLog.aep.repositories.SetorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetorService {

    @Autowired
    private SetorRepository setorRepository;

    public List<SetorModel> listarPorUsuario(String usuarioId) {
        return setorRepository.findByUsuarioId(usuarioId);
    }

    public SetorModel salvar(SetorModel setor) {
        return setorRepository.save(setor);
    }
}