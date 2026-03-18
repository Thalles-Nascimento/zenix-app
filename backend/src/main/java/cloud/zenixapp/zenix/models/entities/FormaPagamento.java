package cloud.zenixapp.zenix.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormaPagamento implements Serializable{

    @Serial
    private static final long serialVersionUID = -6394855555482873289L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pagamento_id", nullable = false)
    private Long id;

    @Column(name = "pagamento_descricao", unique = true)
    private String formaPagamento;

    @Column(name = "pagamento_status")
    private int status = 1;

    @Column(name = "pagamento_created_at")
    private LocalDateTime created_at = LocalDateTime.now();

    @Column(name = "pagamento_update_at")
    private LocalDateTime update_at;
}
