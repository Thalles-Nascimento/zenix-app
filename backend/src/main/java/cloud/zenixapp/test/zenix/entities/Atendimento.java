package cloud.zenixapp.test.zenix.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "atendimento")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Atendimento implements Serializable {

    @Serial
    private static final long serialVersionUID = -632158690661873289L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "atendimento_id", nullable = false)
    private Long id;

    @Column(name = "atendimento_descricao", length = 120)
    private String descricao;

    @Column(name = "atendimento_servico", length = 100)
    private String servico;

    @Column(name = "atendimento_valor", length = 25)
    private Double valor;

    @Column(name = "atendimento_data")
    private LocalDateTime date = LocalDateTime.now();

    @Column(name = "atendimento_status")
    private Integer status = 1;


}
