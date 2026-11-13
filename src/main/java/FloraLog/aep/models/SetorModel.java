package FloraLog.aep.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SetorModel {

    private String id;
    private String nome;
    private String descricao;
    private List<SetorPlantaModel> plantas = new ArrayList<>();

    public SetorModel() {
        this.id = UUID.randomUUID().toString();
    }

    public SetorModel(String nome, String descricao) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<SetorPlantaModel> getPlantas() {
        return plantas;
    }

    public void setPlantas(List<SetorPlantaModel> plantas) {
        this.plantas = plantas;
    }
}