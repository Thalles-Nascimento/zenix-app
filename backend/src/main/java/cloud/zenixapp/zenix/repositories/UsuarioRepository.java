package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuarios, String> {

    UserDetails findByEmail(String email);

    @Query(value = "SELECT * FROM usuarios WHERE tenant_id = :tenant", nativeQuery = true)
    List<Usuarios> findAllByTenants(@Param("tenant") String tenant);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    @Query(value = "SELECT status FROM Usuarios WHERE email = :email")
    int querieStatusUser(@Param("email") String email);

    @Modifying
    @Query(value = "UPDATE usuarios SET usuario_status = -1 WHERE usuario_id = :id and tenant_id = :tenantId", nativeQuery = true)
    void deleteLogico(@Param("id") String id, @Param("tenantId") String tenantId);

    @Modifying
    @Query(value = "UPDATE usuarios SET usuario_status = 1 WHERE usuario_id = :id and tenant_id = :tenantId", nativeQuery = true)
    void ativarUsuario(@Param("id") String id, @Param("tenantId") String tenantId);

    @Query("SELECT u FROM Usuarios u WHERE u.unidade.id = :unidadeId AND u.status = 1")
    List<Usuarios> findBarbeirosByUnidade(@Param("unidadeId") String unidadeId);

    @Query(value = "SELECT * FROM usuarios WHERE usuario_id = :id AND tenant_id = :tenantId", nativeQuery = true)
    Optional<Usuarios> findByIdAndTenants(@Param("id") String id, @Param("tenantId") String tenantId);

    @Query("SELECT u FROM Usuarios u WHERE u.unidade.id = :unidadeId AND u.status = 1 AND u.tenant = :tenantId")
    List<Usuarios> findBarbeirosByUnidadeWithTenant(@Param("unidadeId") String unidadeId, @Param("tenantId") String tenantId);

}
