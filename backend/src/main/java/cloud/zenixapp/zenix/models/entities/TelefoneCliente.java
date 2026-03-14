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
@Table(name = "telefones_clientes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelefoneCliente implements Serializable {

    @Serial
    private static final long serialVersionUID = -6898958690661873289L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "telefone_id", nullable = false)
    private Long id;

    @Column(name = "telefone_cliente", unique = true)
    private String telefoneCliente;

    @OneToMany(mappedBy = "telefoneCliente")
    @JsonIgnore
    private List<Clientes> clientes;

    //  TODO Criar mais colunas - created_at, delete_at e update_at

    public TelefoneCliente(String telefone){
        this.telefoneCliente = telefone;
    }

}
