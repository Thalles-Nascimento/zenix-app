package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.entities.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository  extends JpaRepository<Usuarios, Long> {

    Optional<Usuarios> findByEmail(String email);

}
