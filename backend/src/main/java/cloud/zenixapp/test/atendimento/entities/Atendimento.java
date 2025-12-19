package cloud.zenixapp.test.atendimento.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "atendimento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "atendimento_id")
    @NonNull
    private Long id;

    @Column(name = "atendimento_descricao")
    @NonNull
    private String descricao;

    @Column(name = "atendimento_servico")
    @NonNull
    private String servico;

    @Column(name = "atendimento_valor")
    @NonNull
    private Double valor;


}
