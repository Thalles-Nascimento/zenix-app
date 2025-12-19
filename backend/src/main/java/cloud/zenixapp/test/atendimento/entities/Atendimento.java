package cloud.zenixapp.test.atendimento.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "atendimento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "atendimento_id")
    private int id;

    @Column(name = "atendimento_descricao")
    private String descricao;

    @Column(name = "atendimento_servico")
    private String servico;

    @Column(name = "atendimento_valor")
    private double valor;


}
