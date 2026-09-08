package FloraLog.aep.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PlantaModel {

    @Id
    private String id;

    private String nome;
    private String nomeCientifico;
    private String categoria;
    private String descricao;
    private String nivelDificuldade;
    private String iluminacao;
    private String rega;
    private String temperatura;

    public PlantaModel() {
    }

    public PlantaModel(
            String nome,
            String nomeCientifico,
            String categoria,
            String descricao,
            String nivelDificuldade,
            String iluminacao,
            String rega,
            String temperatura
    ) {

        this.nome = nome;
        this.nomeCientifico = nomeCientifico;
        this.categoria = categoria;
        this.descricao = descricao;
        this.nivelDificuldade = nivelDificuldade;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNivelDificuldade() {
        return nivelDificuldade;
    }

    public void setNivelDificuldade(String nivelDificuldade) {
        this.nivelDificuldade = nivelDificuldade;
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
}