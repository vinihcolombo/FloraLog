package FloraLog.aep.repositories;

import FloraLog.aep.models.SetorPlantaModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetorPlantaRepository extends MongoRepository<SetorPlantaModel, String> {
    List<SetorPlantaModel> findBySetorId(String setorId);
}