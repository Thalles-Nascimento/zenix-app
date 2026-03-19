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
@Table(name = "unidades")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Unidades extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unidade_id", nullable = false)
    private Long id;

    @Column(name = "unidade_nome", length = 100, unique = true)
    private String nomeUnidade;

    @Column(name = "unidade_endereco")
    private String endereco;

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false, updatable = false)
    private Tenants tenant;

    @OneToMany(mappedBy = "unidade")
    @JsonIgnore
    private List<Usuarios> usuarios;

    @Column(name = "unidade_status", nullable = false)
    private Integer status = 1;
}
