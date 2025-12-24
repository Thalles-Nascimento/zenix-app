package cloud.zenixapp.test.zenix.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "atendimento")
@Data
@AllArgsConstructor
public class Atendimento implements Serializable {

    @Serial
    private static final long serialVersionUID = -632158690661873289L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "atendimento_id")
    private Long id;

    @Column(name = "atendimento_descricao")
    private String descricao;

    @Column(name = "atendimento_servico")
    private String servico;

    @Column(name = "atendimento_valor")
    private Double valor;


}
