package cloud.zenixapp.zenix.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuarios")
@Entity
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id", nullable = false)
    private long id;

    @Column(name = "usuario_nome", length = 255)
    private String nome;

    @Column(name = "usuario_email", length = 255, unique = true)
    private String email;

    @Column(name = "usuario_cpf", length = 20, unique = true)
    private String cpf;

    @Column(name = "usuario_grupo")
    private String grupo = "User";

    @Column(name = "usuario_senha")
    private String senha;

    @Column(name = "usuario_status")
    private int status = 1;

}
