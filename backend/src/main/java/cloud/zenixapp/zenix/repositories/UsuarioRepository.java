package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;


public interface UsuarioRepository extends JpaRepository<Usuarios, Long> {

    UserDetails findByEmail(String email);

    @Query(value = "SELECT status FROM Usuarios WHERE email = :email")
    int findByEmailEntities(@Param("email") String email);

    @Modifying
    @Query(value = "UPDATE Usuarios SET status = -1 WHERE id = :id")
    void deleteLogico(@Param("id") Long id);

}
