package FloraLog.aep.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "tarefas")
public class TarefaModel {

    @Id
    private String id;

    private String setorPlantaId;
    private String usuarioId;
    private String descricao;
    private String tipo;
    private String frequencia;
    private LocalDate dataVencimento;
    private boolean concluida;

    public TarefaModel() {
        this.concluida = false;
    }

    public TarefaModel(String setorPlantaId, String usuarioId, String descricao, String tipo, String frequencia, LocalDate dataVencimento) {
        this.setorPlantaId = setorPlantaId;
        this.usuarioId = usuarioId;
        this.descricao = descricao;
        this.tipo = tipo;
        this.frequencia = frequencia;
        this.dataVencimento = dataVencimento;
        this.concluida = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSetorPlantaId() { return setorPlantaId; }
    public void setSetorPlantaId(String setorPlantaId) { this.setorPlantaId = setorPlantaId; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getFrequencia() { return frequencia; }
    public void setFrequencia(String frequencia) { this.frequencia = frequencia; }

    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    public boolean isConcluida() { return concluida; }
    public void setConcluida(boolean concluida) { this.concluida = concluida; }
}