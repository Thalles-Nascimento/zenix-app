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
    @NativeQuery(value = "UPDATE unidades SET unidade_status = -1 WHERE id = :id AND tenant_id = :tenantId")
    void deleteLogico(@Param("id") String id, @Param("tenantId") String tenantId);

    @Modifying
    @NativeQuery(value = "UPDATE unidades SET unidade_status = 1 WHERE id = :id AND tenant_id = :tenantId")
    void ativarUnidade(@Param("id") String id, @Param("tenantId")String tenantId);

    boolean existsByNomeUnidadeAndTenant(String nomeUnidade, String tenant);

    @NativeQuery(
            value = "SELECT un.id AS id," +
            "un.unidade_nome AS nome," +
            "un.unidade_endereco AS endereco," +
            "un.unidade_status AS status," +
            "un.updated_at AS updateAt," +
            "un.deleted_at AS deleteAt " +
            "FROM unidades un WHERE tenant_id = :tenantId")
    List<UnidadeSimplesView> findUnidadesByTenant(@Param("tenantId") String tenantId);

//  TODO retirar o delete_at da consulta
    @NativeQuery(
            value = "SELECT un.id AS id, " +
            "un.unidade_nome AS nome," +
            "un.unidade_endereco AS endereco," +
            "un.unidade_status AS status," +
            "un.updated_at AS updateAt," +
            "un.deleted_at AS deleteAt " +
            "FROM unidades un WHERE un.id = :id AND un.tenant_id = :tenantId")
    Optional<UnidadeSimplesView> findById(@Param("id") String id, @Param("tenantId") String tenantId);

}
