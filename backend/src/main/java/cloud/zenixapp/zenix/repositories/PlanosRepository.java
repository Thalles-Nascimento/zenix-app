package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Planos;
import cloud.zenixapp.zenix.models.interfaces.PlanosView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlanosRepository extends JpaRepository<Planos, String> {

    @NativeQuery(
            value = "SELECT " +
                    "p.id AS id, " +
                    "p.planos_descricao AS descricao, " +
                    "p.planos_servico AS servico," +
                    "p.planos_valor AS valor, " +
                    "p.planos_limite AS atendimentos " +
                    "FROM planos p " +
                    "WHERE p.tenant_id = :tenantId")
    List<PlanosView> findAll(@Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT " +
                    "p.id AS id, " +
                    "p.planos_descricao AS descricao, " +
                    "p.planos_servico AS servico," +
                    "p.planos_valor AS valor, " +
                    "p.planos_limite AS atendimentos " +
                    "FROM planos p " +
                    "WHERE p.id = :id AND p.tenant_id = :tenantId")
    Optional<PlanosView> findById(@Param("id") String id, @Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT * " +
                    "FROM planos p " +
                    "WHERE p.id = :id AND p.tenant_id = :tenantId")
    Optional<Planos> findPlanosById(@Param("id") String id, @Param("tenantId") String tenantId);

}
