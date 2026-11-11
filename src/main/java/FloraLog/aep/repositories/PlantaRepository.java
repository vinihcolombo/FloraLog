package FloraLog.aep.repositories;

import FloraLog.aep.models.PlantaModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlantaRepository extends MongoRepository<PlantaModel, String> {

    List<PlantaModel> findByUsuarioId(String usuarioId);

    Optional<PlantaModel> findByCodigoPartilha(String codigoPartilha);
}