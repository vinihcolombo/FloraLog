package FloraLog.aep.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SetorPlantaModel {

    private String id;
    private String plantaBibliotecaId;
    private String nome;
    private String nomeCientifico;
    private String fotoUrl;
    private String iluminacao;
    private String rega;
    private String temperatura;
    private List<TarefaModel> tarefas = new ArrayList<>();

    public SetorPlantaModel() {
        this.id = UUID.randomUUID().toString();
    }

    public SetorPlantaModel(String plantaBibliotecaId, String nome, String nomeCientifico, String fotoUrl, String iluminacao, String rega, String temperatura) {
        this.id = UUID.randomUUID().toString();
        this.plantaBibliotecaId = plantaBibliotecaId;
        this.nome = nome;
        this.nomeCientifico = nomeCientifico;
        this.fotoUrl = fotoUrl;
        this.iluminacao = iluminacao;
        this.rega = rega;
        this.temperatura = temperatura;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlantaBibliotecaId() {
        return plantaBibliotecaId;
    }

    public void setPlantaBibliotecaId(String plantaBibliotecaId) {
        this.plantaBibliotecaId = plantaBibliotecaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeCientifico() {
        return nomeCientifico;
    }

    public void setNomeCientifico(String nomeCientifico) {
        this.nomeCientifico = nomeCientifico;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getIluminacao() {
        return iluminacao;
    }

    public void setIluminacao(String iluminacao) {
        this.iluminacao = iluminacao;
    }

    public String getRega() {
        return rega;
    }

    public void setRega(String rega) {
        this.rega = rega;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public List<TarefaModel> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<TarefaModel> tarefas) {
        this.tarefas = tarefas;
    }
}