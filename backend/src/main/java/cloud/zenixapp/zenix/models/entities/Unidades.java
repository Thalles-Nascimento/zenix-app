package cloud.zenixapp.zenix.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    @Column(name = "unidade_nome", length = 100)
    private String nomeUnidade;

    @Column(name = "unidade_endereco")
    private String endereco;

    @OneToMany(mappedBy = "unidade")
    @JsonIgnore
    private List<Usuarios> usuarios;

    @Column(name = "unidade_status", nullable = false)
    private Integer status = 1;
}
