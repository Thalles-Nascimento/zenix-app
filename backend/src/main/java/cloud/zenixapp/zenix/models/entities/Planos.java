package cloud.zenixapp.zenix.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "planos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Planos implements Serializable{

    @Serial
    private static final long serialVersionUID = -6394855555482873289L;

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

    @Column(name = "planos_created_at")
    private LocalDateTime created_at = LocalDateTime.now();

    @Column(name = "planos_update_at")
    private LocalDateTime update_at;

    @OneToMany(mappedBy = "planos", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Clientes> clientes;
}
