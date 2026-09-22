package cloud.zenixapp.zenix.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "pagamentos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormaPagamento extends BaseEntity{

    @Column(name = "pagamento_descricao")
    private String formaPagamento;

    @Column(name = "pagamento_status")
    private int status = 1;
}
