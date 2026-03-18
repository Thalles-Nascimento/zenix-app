package cloud.zenixapp.zenix.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Clientes implements Serializable {

    @Serial
    private static final long serialVersionUID = -6394858658282873289L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id", nullable = false)
    private Long id;

    @Column(name = "cliente_nome", length = 120, unique = true)
    private String nomeCliente;

    @ManyToOne
    @JoinColumn(name = "telefone_id")
    private TelefoneCliente telefoneCliente;

    @Column(name = "cliente_retorno")
    private int totalRetornos = 1;

    @Column(name = "cliente_created")
    private LocalDateTime created_at = LocalDateTime.now();

    //  TODO Criar mais colunas - delete_at e update_at

}
