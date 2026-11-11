package FloraLog.aep.repositories;

import FloraLog.aep.models.SetorModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetorRepository extends MongoRepository<SetorModel, String> {
    List<SetorModel> findByUsuarioId(String usuarioId);
}