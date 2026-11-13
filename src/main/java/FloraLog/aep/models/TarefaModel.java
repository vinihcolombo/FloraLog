package FloraLog.aep.models;

import org.springframework.data.annotation.Id;
import java.time.LocalDate;
import java.util.UUID;

public class TarefaModel {

    @Id
    private String id = UUID.randomUUID().toString().substring(0, 8);

    private String descricao;
    private String tipo;
    private String frequencia;
    private LocalDate dataVencimento;
    private boolean concluida;

    public TarefaModel() {
        this.concluida = false;
    }

    public TarefaModel(String descricao, String tipo, String frequencia, LocalDate dataVencimento) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.descricao = descricao;
        this.tipo = tipo;
        this.frequencia = frequencia;
        this.dataVencimento = dataVencimento;
        this.concluida = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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