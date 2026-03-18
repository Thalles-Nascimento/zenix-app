package cloud.zenixapp.zenix.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "servicos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Servicos implements Serializable{

    @Serial
    private static final long serialVersionUID = -6394858655482873289L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "servico_id", nullable = false)
    private Long id;

    @Column(name = "servico_descricao", unique = true)
    private String servico;

    @Column(name = "servico_valor")
    private Double valor;

    @Column(name = "servico_status")
    private int status = 1;

    @Column(name = "servico_created_at")
    private LocalDateTime created_at = LocalDateTime.now();

    @Column(name = "servico_update_at")
    private LocalDateTime update_at;
}
