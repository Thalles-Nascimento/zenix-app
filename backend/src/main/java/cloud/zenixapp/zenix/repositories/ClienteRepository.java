package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.dtos.responses.clientes.ClientePlanosResumoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.clientes.ClienteSimplesResponseDTO;
import cloud.zenixapp.zenix.models.entities.Clientes;
import cloud.zenixapp.zenix.models.entities.TelefoneCliente;
import cloud.zenixapp.zenix.models.interfaces.ClientesProjectionView;
import cloud.zenixapp.zenix.models.interfaces.ClientesSimplesProjectionView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Clientes, String> {

//  Listar clientes pelo Nome
    @Query("""
        SELECT new cloud.zenixapp.zenix.models.dtos.responses.clientes.
                ClientePlanosResumoResponseDTO(
                    c.id,
                    c.nomeCliente,
                    new cloud.zenixapp.zenix.models.dtos.responses.telefones.TelefoneClienteResponseDTO(
                        tc.telefoneCliente
                    ),
                    c.dataRenovacao,
                    c.atendimentosMes,
                    c.totalRetornos,
                    c.status,
                    new cloud.zenixapp.zenix.models.dtos.responses.planos.PlanosClienteResumoResponseDTO(
                        p.id, p.planoDescricao
                    )
                )
                FROM Clientes c
                LEFT JOIN c.telefoneCliente tc
                LEFT JOIN c.planos p
                WHERE c.nomeCliente = :nome AND c.tenant = :tenantId
        """)
    Optional<ClientePlanosResumoResponseDTO> findByName(@Param("nome") String nome, @Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT " +
                    "c.id AS id," +
                    "c.cliente_nome AS nome," +
                    "tc.telefone_cliente AS telefone," +
                    "c.cliente_data_renovacao AS dataRenovacao," +
                    "c.cliente_atendimentos_mes AS atendimentoMes," +
                    "c.cliente_retorno AS retorno," +
                    "c.updated_at AS updatedAt," +
                    "c.cliente_status AS status," +
                    "p.id AS planoId," +
                    "p.planos_descricao AS planoDescricao," +
                    "p.planos_valor AS planoValor," +
                    "p.planos_servico AS planoServicoRaw," +
                    "p.planos_limite AS planoAtendimentos " +
                    "FROM clientes c " +
                    "LEFT JOIN telefones_clientes tc ON c.telefone_id = tc.id " +
                    "LEFT JOIN planos p ON c.planos_id = p.id " +
                    "WHERE c.cliente_nome LIKE %:nome% AND c.tenant_id = :tenantId AND c.cliente_status = 1")
    List<ClientesProjectionView> findByNameContaining(@Param("nome") String nome, @Param("tenantId") String tenantId);

    @Modifying
    @NativeQuery("UPDATE clientes SET cliente_atendimentos_mes = 0 WHERE tenant_id = :tenantId AND planos_id IS NOT NULL AND DAY(cliente_data_renovacao) = :dia")
    void resetarAtendimentosMes(@Param("dia") int dia, @Param("tenantId") String tenantId);

    @Modifying
    @NativeQuery(
            value = "UPDATE clientes c " +
                    "SET c.cliente_atendimentos_mes = c.cliente_atendimentos_mes + 1 " +
                    "WHERE c.tenant_id = :tenantId AND c.id = :id")
    void atualizarAtendimentosMes(@Param("id") String id, @Param("tenantId") String tenantId);

    @Modifying
    @NativeQuery(
            value = "UPDATE clientes c " +
                    "SET c.cliente_retorno = c.cliente_retorno + 1 " +
                    "WHERE c.id = :id AND c.tenant_id = :tenantId")
    void atualizarRetorno(@Param("id") String id, @Param("tenantId") String tenantId);

    @Modifying
    @NativeQuery(
            value = "UPDATE clientes c " +
                    "SET c.cliente_retorno = c.cliente_retorno - 1 " +
                    "WHERE c.id = :id AND c.tenant_id = :tenantId")
    void retirarRetorno(@Param("id") String id, @Param("tenantId") String tenantId);

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

    @Query(
            value = "SELECT tc " +
                    "FROM TelefoneCliente tc " +
                    "WHERE tc.telefoneCliente = :telefoneCliente AND tc.tenant = :tenant")
    Optional<TelefoneCliente> findByTelefone_ClienteAndTenant(String telefoneCliente, String tenant);

    @Query("""
        SELECT new cloud.zenixapp.zenix.models.dtos.responses.clientes.
                ClienteSimplesResponseDTO(
                    c.id,
                    c.nomeCliente,
                    new cloud.zenixapp.zenix.models.dtos.responses.telefones.TelefoneClienteResponseDTO(
                        tc.telefoneCliente
                    ),
                    c.status,
                    new cloud.zenixapp.zenix.models.dtos.responses.planos.PlanosClienteResumoResponseDTO(
                        p.id, p.planoDescricao
                    )
                )
                FROM Clientes c
                LEFT JOIN c.telefoneCliente tc
                LEFT JOIN c.planos p
                WHERE tc.telefoneCliente = :telefone AND c.tenant = :tenantId
        """)
    List<ClienteSimplesResponseDTO> findClientByNumber(@Param("telefone") String telefone, @Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT " +
                    "c.id AS id," +
                    "c.cliente_nome AS nome," +
                    "tc.telefone_cliente AS telefone," +
                    "c.cliente_data_renovacao AS dataRenovacao," +
                    "c.cliente_atendimentos_mes AS atendimentoMes," +
                    "c.cliente_retorno AS retorno," +
                    "c.updated_at AS updatedAt," +
                    "c.cliente_status AS status," +
                    "p.id AS planoId," +
                    "p.planos_descricao AS planoDescricao," +
                    "p.planos_valor AS planoValor," +
                    "p.planos_limite AS planoAtendimentos, " +
                    "p.planos_servico AS planoServicoRaw " +
                    "FROM clientes c " +
                    "LEFT JOIN telefones_clientes tc ON c.telefone_id = tc.id " +
                    "LEFT JOIN planos p ON c.planos_id = p.id " +
                    "WHERE c.tenant_id = :tenantId")
    List<ClientesProjectionView> findAll(@Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT " +
                    "c.id AS id," +
                    "c.cliente_nome AS nome," +
                    "tc.telefone_cliente AS telefone," +
                    "c.cliente_data_renovacao AS dataRenovacao," +
                    "c.cliente_atendimentos_mes AS atendimentoMes," +
                    "c.cliente_retorno AS retorno," +
                    "c.updated_at AS updatedAt," +
                    "c.cliente_status AS status," +
                    "p.id AS planoId," +
                    "p.planos_descricao AS planoDescricao," +
                    "p.planos_valor AS planoValor," +
                    "p.planos_limite AS planoAtendimentos, " +
                    "p.planos_servico AS planoServicoRaw " +
                    "FROM clientes c " +
                    "LEFT JOIN telefones_clientes tc ON c.telefone_id = tc.id " +
                    "LEFT JOIN planos p ON c.planos_id = p.id " +
                    "WHERE c.id = :id AND c.tenant_id = :tenantId AND c.cliente_status = 1")
    Optional<ClientesProjectionView> findById(@Param("id") String id, @Param("tenantId") String tenantId);

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
                    "LEFT JOIN telefones_clientes tc ON c.telefone_id = tc.id " +
                    "WHERE c.id = :id AND c.tenant_id = :tenantId")
    Optional<ClientesSimplesProjectionView> findByIdSimples(@Param("id") String id, @Param("tenantId") String tenantId);

}