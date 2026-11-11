package FloraLog.aep.repositories;

import FloraLog.aep.models.TarefaModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarefaRepository extends MongoRepository<TarefaModel, String> {
    List<TarefaModel> findBySetorPlantaId(String setorPlantaId);
}