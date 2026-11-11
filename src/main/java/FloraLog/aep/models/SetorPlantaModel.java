package FloraLog.aep.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "setor_plantas")
public class SetorPlantaModel {

    @Id
    private String id;

    private String setorId;
    private String plantaId;
    private String usuarioId;

    public SetorPlantaModel() {
    }

    public SetorPlantaModel(String setorId, String plantaId, String usuarioId) {
        this.setorId = setorId;
        this.plantaId = plantaId;
        this.usuarioId = usuarioId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSetorId() {
        return setorId;
    }

    public void setSetorId(String setorId) {
        this.setorId = setorId;
    }

    public String getPlantaId() {
        return plantaId;
    }

    public void setPlantaId(String plantaId) {
        this.plantaId = plantaId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }
}