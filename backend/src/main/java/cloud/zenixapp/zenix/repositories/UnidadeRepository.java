package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Unidades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UnidadeRepository extends JpaRepository<Unidades, Long> {

    @Modifying
    @Query(value = "UPDATE unidades SET unidade_status = -1 WHERE unidade_id = :id AND tenant_id = :tenantId", nativeQuery = true)
    void deleteLogico(@Param("id") Long id, @Param("tenantId") String tenantId);

    @Modifying
    @Query(value = "UPDATE unidades SET unidade_status = 1 WHERE unidade_id = :id AND tenant_id = :tenantId", nativeQuery = true)
    void ativarUnidade(@Param("id") Long id, @Param("tenantId")String tenantId);

    @Query(value = "SELECT * FROM unidades WHERE unidade_nome = :nome AND tenant_id = :tenantId", nativeQuery = true)
    Optional<Unidades> buscarUnidadesPorTenant(@Param("nome") String nome, @Param("tenantId") String tenantId);
}
