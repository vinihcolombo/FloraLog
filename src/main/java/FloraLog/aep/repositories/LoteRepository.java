package FloraLog.aep.repositories;

import FloraLog.aep.models.LoteModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoteRepository extends MongoRepository<LoteModel, String> {
    List<LoteModel> findByHortaId(String hortaId);
}