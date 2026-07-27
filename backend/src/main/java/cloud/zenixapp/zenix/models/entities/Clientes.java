package cloud.zenixapp.zenix.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "clientes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Clientes extends BaseEntity {

    @Column(name = "cliente_nome", length = 120, unique = true)
    private String nomeCliente;

    @ManyToOne
    @JoinColumn(name = "telefone_id")
    private TelefoneCliente telefoneCliente;

    @Column(name = "cliente_retorno")
    private int totalRetornos = 0;

    @Column(name = "cliente_status", nullable = false, columnDefinition = "INT DEFAULT 1")
    private int status = 1;

    @ManyToOne
    @JoinColumn(name = "planos_id")
    private Planos planos;

    @Column(name = "cliente_atendimentos_mes", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer atendimentosMes;

    @Column(name = "cliente_data_renovacao")
    private LocalDate dataRenovacao;


}
