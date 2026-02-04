package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.entities.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository  extends JpaRepository<Usuarios, Long> {
}
