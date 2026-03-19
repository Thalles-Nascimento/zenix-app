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
import java.util.UUID;

@Entity
@Table(name = "empresas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Empresas implements Serializable {

    @Serial
    private static final long serialVersionUID = -1234567890123456789L;

    @Id
    @Column(name = "empresa_id", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "empresa_nome", length = 100, unique = true, nullable = false)
    private String nome;

    @Column(name = "empresa_slug", length = 100, unique = true, nullable = false)
    private String slug;

    @Column(name = "empresa_status")
    private int status = 1;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "empresa")
    @JsonIgnore
    private List<Unidades> unidades;

    @PrePersist
    protected void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}