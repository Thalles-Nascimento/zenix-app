package cloud.zenixapp.zenix.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "planos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Planos extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planos_id", nullable = false)
    private Long id;

    @Column(name = "planos_descricao", unique = true)
    private String planoDescricao;

    @Column(name = "planos_valor")
    private Double valor;

    @Column(name = "planos_limite")
    private int limiteAtendimentos;

    @Column(name = "planos_status")
    private int status = 1;

    @OneToMany(mappedBy = "planos", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Clientes> clientes;
}
