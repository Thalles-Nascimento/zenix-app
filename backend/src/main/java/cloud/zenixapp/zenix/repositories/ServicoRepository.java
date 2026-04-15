package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Servicos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServicoRepository extends JpaRepository<Servicos, String> {

    boolean existsServicoByServicoAndTenantId(String servico, String tenantId);

    @NativeQuery(value = "SELECT servico_descricao, servico_valor, servico_status FROM servicos WHERE id = :id AND tenant_id = :tenantId")
    Optional<Servicos> findById(@Param("id") String id, @Param("tenantId") String tenantId);

    @NativeQuery(value = "SELECT id, servico_descricao, servico_valor, servico_status FROM servicos WHERE tenant_id = :tenantId")
    List<Servicos> findAll(@Param("tenantId") String tenantId);

}
