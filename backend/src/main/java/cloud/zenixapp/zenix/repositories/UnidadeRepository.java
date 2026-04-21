package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Unidades;
import cloud.zenixapp.zenix.models.interfaces.UnidadeSimplesView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UnidadeRepository extends JpaRepository<Unidades, String> {

    @Modifying
    @NativeQuery(value = "UPDATE unidades SET unidade_status = -1 WHERE unidade_id = :id AND tenant_id = :tenantId")
    void deleteLogico(@Param("id") String id, @Param("tenantId") String tenantId);

    @Modifying
    @NativeQuery(value = "UPDATE unidades SET unidade_status = 1 WHERE unidade_id = :id AND tenant_id = :tenantId")
    void ativarUnidade(@Param("id") String id, @Param("tenantId")String tenantId);

    boolean existsByNomeUnidadeAndTenantId(String nomeUnidade, String tenantId);

    @NativeQuery(value = "SELECT * FROM unidades WHERE tenant_id = :tenantId")
    List<Unidades> findUnidadesByTenant(@Param("tenantId") String tenantId);

    @NativeQuery(value = "SELECT * FROM unidades WHERE id = :id AND tenant_id = :tenantId")
    Optional<Unidades> findById(@Param("id") String id, @Param("tenantId") String tenantId);

}
