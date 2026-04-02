package cloud.zenixapp.zenix.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "servicos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Servicos extends BaseEntity{

    @Column(name = "servico_descricao")
    private String servico;

    @Column(name = "servico_valor")
    private Double valor;

    @Column(name = "servico_status")
    private int status = 1;

}
