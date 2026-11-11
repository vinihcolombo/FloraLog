package FloraLog.aep.repositories;

import FloraLog.aep.models.HortaModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HortaRepository extends MongoRepository<HortaModel, String> {
}