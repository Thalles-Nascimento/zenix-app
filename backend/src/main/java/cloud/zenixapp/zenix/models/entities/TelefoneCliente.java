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
@Table(name = "telefones_clientes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelefoneCliente extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "telefone_id", nullable = false)
    private Long id;

    @Column(name = "telefone_cliente", unique = true)
    private String telefoneCliente;

    @OneToMany(mappedBy = "telefoneCliente")
    @JsonIgnore
    private List<Clientes> clientes;


    public TelefoneCliente(String telefone){
        this.telefoneCliente = telefone;
    }

}
