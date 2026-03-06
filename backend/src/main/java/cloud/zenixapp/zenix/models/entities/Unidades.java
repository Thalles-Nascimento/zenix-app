package cloud.zenixapp.zenix.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "unidades")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Unidades implements Serializable {

    @Serial
    private static final long serialVersionUID = -6394858690665251519L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unidade_id", nullable = false)
    private Long id;

    @Column(name = "unidade_nome", length = 100, unique = true)
    private String nomeUnidade;

    @Column(name = "unidade_endereco")
    private String endereco;

    @OneToMany(mappedBy = "unidade")
    @JsonIgnore
    private List<Usuarios> usuarios;

    @Column(name = "unidade_status", nullable = false)
    private Integer status = 1;
}
