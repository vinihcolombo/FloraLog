package FloraLog.aep.repositories;


import FloraLog.aep.models.PlantaModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantaRepository extends MongoRepository<PlantaModel, String> {
}