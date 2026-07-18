package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Servicos;
import cloud.zenixapp.zenix.models.interfaces.ServicosSimplesView;
import cloud.zenixapp.zenix.models.interfaces.ServicosView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServicoRepository extends JpaRepository<Servicos, String> {

    boolean existsServicoByServicoAndTenantId(String servico, String tenantId);

    void deleteByIdAndTenantId(String id, String tenantId);

    @NativeQuery(
            value = "SELECT " +
                    "s.id AS id, " +
                    "s.servico_descricao AS servico, " +
                    "s.servico_valor AS valor, " +
                    "s.deleted_at AS deleteAt, " +
                    "s.updated_at AS updateAt, " +
                    "s.servico_status AS status " +
                    "FROM servicos s " +
                    "WHERE s.id = :id AND s.tenant_id = :tenantId")
    Optional<ServicosView> findById(@Param("id") String id, @Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT " +
            "s.id AS id, " +
            "s.servico_descricao AS servico, " +
            "s.servico_valor AS valor, " +
            "s.servico_status AS status " +
            "FROM servicos s " +
            "WHERE s.tenant_id = :tenantId")
    List<ServicosSimplesView> findAll(@Param("tenantId") String tenantId);

}
