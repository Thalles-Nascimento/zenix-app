package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Clientes;
import cloud.zenixapp.zenix.models.entities.TelefoneCliente;
import cloud.zenixapp.zenix.models.interfaces.ClientesProjectionView;
import cloud.zenixapp.zenix.models.interfaces.ClientesSimplesView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Clientes, String> {

    @NativeQuery(
            value = "SELECT " +
                    "c.id AS id," +
                    "c.cliente_nome AS nome," +
                    "tc.telefone_cliente AS telefone," +
                    "c.cliente_data_renovacao AS dataRenovacao," +
                    "c.cliente_atendimentos_mes AS atendimentoMes," +
                    "c.cliente_retorno AS retorno," +
                    "c.updated_at AS updatedAt," +
                    "c.planos_id AS planosId," +
                    "c.cliente_status AS status " +
                    "FROM clientes c " +
                    "INNER JOIN telefones_clientes tc ON c.telefone_id = tc.id " +
                    "WHERE c.cliente_nome = :nome AND c.tenant_id = :tenantId AND c.cliente_status = 1")
    ClientesProjectionView findByName(@Param("nome") String nome, @Param("tenantId") String tenantId);

    @Query(value = "SELECT * FROM clientes WHERE cliente_nome LIKE %:nome% AND cliente_status = 1", nativeQuery = true)
    List<Clientes> findByNameContaining(@Param("nome") String nome);

    @Modifying
    @NativeQuery("UPDATE clientes SET atendimentosMes = 0 WHERE tenant_id = :tenantId planos_id IS NOT NULL AND DAY(cliente_data_renovacao) = :dia")
    void resetarAtendimentosMes(@Param("dia") int dia, @Param("tenantId") String tenantId);

    @Modifying
    @NativeQuery(
            value = "UPDATE clientes c " +
                    "SET c.cliente_status = -1, c.deleted_at = :deleteTime " +
                    "WHERE c.id = :id AND c.tenant_id = :tenantId")
    void deleteLogico(@Param("id") String id, @Param("deleteTime") LocalDateTime deleteTime, @Param("tenantId") String tenantId);

    @Modifying
    @NativeQuery(
            value = "UPDATE clientes c " +
                    "SET c.cliente_status = 1, c.deleted_at = null " +
                    "WHERE c.id = :id AND c.tenant_id = :tenantId")
    void ativarCliente(@Param("id") String id, @Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT * " +
                    "FROM telefones_clientes tc " +
                    "WHERE tc.telefone_cliente = :number AND tc.tenant_id = :tenantId")
    Optional<TelefoneCliente> findByNumber(@Param("number") String number, @Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT " +
                    "c.id AS id," +
                    "c.cliente_nome AS nome," +
                    "tc.telefone_cliente AS telefone," +
                    "c.cliente_data_renovacao AS dataRenovacao," +
                    "c.cliente_atendimentos_mes AS atendimentoMes," +
                    "c.cliente_retorno AS retorno," +
                    "c.updated_at AS updatedAt," +
                    "c.cliente_status AS status " +
                    "FROM clientes c " +
                    "INNER JOIN telefones_clientes tc ON c.telefone_id = tc.id " +
                    "WHERE tc.telefone_cliente = :telefone AND c.tenant_id = :tenantId")
    List<ClientesProjectionView> findClientByNumber(@Param("telefone") String telefone, @Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT " +
                    "c.id AS id," +
                    "c.cliente_nome AS nome," +
                    "tc.telefone_cliente AS telefone," +
                    "c.cliente_data_renovacao AS dataRenovacao," +
                    "c.cliente_atendimentos_mes AS atendimentoMes," +
                    "c.cliente_retorno AS retorno," +
                    "c.updated_at AS updatedAt," +
                    "c.cliente_status AS status " +
                    "FROM clientes c " +
                    "INNER JOIN telefones_clientes tc ON c.telefone_id = tc.id " +
                    "WHERE c.id = :id AND c.tenant_id = :tenantId AND c.cliente_status = 1")
    Optional<ClientesProjectionView> findById(@Param("id") String id, @Param("tenantId") String tenantId);

}
