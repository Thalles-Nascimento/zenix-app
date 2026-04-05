package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.dtos.responses.UsuarioResponseDTO;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuarios, String> {

    UserDetails findByEmail(String email);

    @NativeQuery(
            value = "SELECT * FROM usuarios u WHERE u.tenant_id = :tenant"
    )
    List<Usuarios> findAllByTenants(@Param("tenant") String tenant);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    @Query(
            value = "SELECT status FROM Usuarios WHERE email = :email"
    )
    int findStatusByEmail(@Param("email") String email);

    @Modifying
    @NativeQuery(
            value = "UPDATE usuarios SET usuario_status = -1 WHERE id = :id and tenant_id = :tenantId"
    )
    void deleteLogico(@Param("id") String id, @Param("tenantId") String tenantId);

    @Modifying
    @NativeQuery(
            value = "UPDATE usuarios SET usuario_status = 1 WHERE id = :id and tenant_id = :tenantId"
    )
    void ativarUsuario(@Param("id") String id, @Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT id, usuario_nome FROM usuarios WHERE usuarios_unidade = :unidadeId AND usuario_status = 1"
    )
    List<Usuarios> findBarbeirosByUnidade(@Param("unidadeId") String unidadeId);

//   TODO - Refazer a Query
    @Lock(LockModeType.OPTIMISTIC)
    @Query(
            value = "SELECT u FROM Usuarios u WHERE u.id = :id AND u.tenantId = :tenantId"
    )
    Optional<Usuarios> findById(@Param("id") String id, @Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT id, usuario_email, usuario_nome, usuario_cpf, usuario_grupo, usuario_status, usuarios_unidade" +
            " FROM usuarios WHERE usuarios_unidade = :unidadeId AND usuario_status = 1 AND tenant_id = :tenantId"
    )
    List<Usuarios> findBarbeirosByUnidadeAndTenant(@Param("unidadeId") String unidadeId, @Param("tenantId") String tenantId);

}
