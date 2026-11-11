package FloraLog.aep.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "lotes")
public class LoteModel {

    @Id
    private String id;

    private String nome;
    private String hortaId;
    private String titularId;
    private List<String> colaboradoresIds;
    private List<String> plantasIds;

    public LoteModel() {
    }

    public LoteModel(String nome, String hortaId, String titularId, List<String> colaboradoresIds, List<String> plantasIds) {
        this.nome = nome;
        this.hortaId = hortaId;
        this.titularId = titularId;
        this.colaboradoresIds = colaboradoresIds;
        this.plantasIds = plantasIds;
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

    public String getHortaId() {
        return hortaId;
    }

    public void setHortaId(String hortaId) {
        this.hortaId = hortaId;
    }

    public String getTitularId() {
        return titularId;
    }

    public void setTitularId(String titularId) {
        this.titularId = titularId;
    }

    public List<String> getColaboradoresIds() {
        return colaboradoresIds;
    }

    public void setColaboradoresIds(List<String> colaboradoresIds) {
        this.colaboradoresIds = colaboradoresIds;
    }

    public List<String> getPlantasIds() {
        return plantasIds;
    }

    public void setPlantasIds(List<String> plantasIds) {
        this.plantasIds = plantasIds;
    }
}