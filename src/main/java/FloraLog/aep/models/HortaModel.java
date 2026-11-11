package FloraLog.aep.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "hortas")
public class HortaModel {

    @Id
    private String id;

    private String nome;
    private int capacidadeMaximaLotes;
    private List<String> donosIds;
    private List<String> gerentesIds;

    public HortaModel() {
    }

    public HortaModel(String nome, int capacidadeMaximaLotes, List<String> donosIds, List<String> gerentesIds) {
        this.nome = nome;
        this.capacidadeMaximaLotes = capacidadeMaximaLotes;
        this.donosIds = donosIds;
        this.gerentesIds = gerentesIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCapacidadeMaximaLotes() {
        return capacidadeMaximaLotes;
    }

    public void setCapacidadeMaximaLotes(int capacidadeMaximaLotes) {
        this.capacidadeMaximaLotes = capacidadeMaximaLotes;
    }

    public List<String> getDonosIds() {
        return donosIds;
    }

    public void setDonosIds(List<String> donosIds) {
        this.donosIds = donosIds;
    }

    public List<String> getGerentesIds() {
        return gerentesIds;
    }

    public void setGerentesIds(List<String> gerentesIds) {
        this.gerentesIds = gerentesIds;
    }
}