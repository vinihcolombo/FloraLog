package FloraLog.aep.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "usuarios")
public class UsuarioModel {

    @Id
    private String id;

    private String nome;
    private String email;
    private String senha;
    private String avatarUrl;
    private PerfilUsuario perfil;

    // Árvore NoSQL centralizada no Utilizador
    private List<PlantaModel> bibliotecaPlantas = new ArrayList<>();
    private List<SetorModel> setores = new ArrayList<>();

    public enum PerfilUsuario {
        DONO,
        GERENTE,
        COLABORADOR
    }

    public UsuarioModel() {
    }

    public UsuarioModel(String nome, String email, String senha, String avatarUrl, PerfilUsuario perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.avatarUrl = avatarUrl;
        this.perfil = perfil;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public List<PlantaModel> getBibliotecaPlantas() {
        return bibliotecaPlantas;
    }

    public void setBibliotecaPlantas(List<PlantaModel> bibliotecaPlantas) {
        this.bibliotecaPlantas = bibliotecaPlantas;
    }

    public List<SetorModel> getSetores() {
        return setores;
    }

    public void setSetores(List<SetorModel> setores) {
        this.setores = setores;
    }
}