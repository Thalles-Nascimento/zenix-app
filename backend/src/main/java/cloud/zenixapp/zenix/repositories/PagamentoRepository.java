package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.FormaPagamento;
import cloud.zenixapp.zenix.models.interfaces.FormaPagamentoView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<FormaPagamento, String> {

    @NativeQuery(
            value = "SELECT " +
                    "p.id AS id, " +
                    "p.pagamento_descricao AS pagamento, " +
                    "p.updated_at AS updatedAt, " +
                    "p.pagamento_status AS status " +
                    "FROM pagamentos p " +
                    "WHERE p.tenant_id = :tenantId")
    List<FormaPagamentoView> findAll(@Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT " +
                    "p.id AS id, " +
                    "p.pagamento_descricao AS pagamento," +
                    "p.updated_at AS updatedAt, " +
                    "p.pagamento_status AS status " +
                    "FROM pagamentos p " +
                    "WHERE p.id = :id AND p.tenant_id = :tenantId")
    Optional<FormaPagamentoView> findById(@Param("id") String id, @Param("tenantId") String tenantId);

}
