package cloud.zenixapp.zenix.models.entities;


import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="usuarios")
@Entity
public class Usuarios implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = -6321586946159484859L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id", nullable = false)
    private Long id;

    @Column(name = "usuario_nome")
    private String nome;

    @Column(name = "usuario_email", unique = true)
    private String email;

    @Column(name = "usuario_senha")
    private String senha;

    @Column(name = "usuario_cpf", unique = true)
    private String cpf;

    @Column(name = "usuario_grupo")
    private UsuariosRoleEnum grupo;

    @OneToMany(mappedBy = "usuarios")
    private List<Atendimento> atendimentos;

    @OneToMany(mappedBy = "usuario")
    private List<Fila> filaClientes;

    @Column(name = "usuario_status")
    private int status = 1;

    private boolean enabled = true;


    public Usuarios(String nome, String email, String senha, String cpf, UsuariosRoleEnum grupo) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cpf = cpf;
        this.grupo = grupo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.grupo == UsuariosRoleEnum.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));

    }

    @Override
    public @Nullable String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (enabled){
            return true;
        }
        return false;
    }
}
