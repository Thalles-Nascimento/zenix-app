package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.entities.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;


public interface UsuarioRepository extends JpaRepository<Usuarios, Long> {

    UserDetails findByEmail(String email);

}
