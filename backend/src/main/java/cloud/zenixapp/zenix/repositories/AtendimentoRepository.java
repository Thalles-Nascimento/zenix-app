package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.models.interfaces.AtendimentoAndUsuarioProjectionView;
import cloud.zenixapp.zenix.models.interfaces.AtendimentoProjectionView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AtendimentoRepository extends JpaRepository<Atendimento, String> {

    @Modifying
    @NativeQuery(
            value = "UPDATE atendimentos a " +
                    "SET a.atendimento_status = -1, a.deleted_at = :deleteTime " +
                    "WHERE a.id = :id AND a.tenant_id = :tenantId")
    void deleteLogico(@Param("id") String id, @Param("deleteTime") LocalDateTime deleteTime, @Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT " +
                    "a.id AS id, " +
                    "a.atendimento_descricao AS descricao," +
                    "a.atendimento_servico AS servicoRaw," +
                    "a.atendimento_valor AS valor," +
                    "a.atendimento_pagamento AS formaPagamento," +
                    "a.atendimento_data AS data," +
                    "a.atendimento_observacao AS observacao," +
                    "a.atendimento_status AS status, " +
                    "a.updated_at AS updatedAt, " +
                    "a.usuario_id AS usuarioId " +
                    "FROM atendimentos a " +
                    "WHERE a.usuario_id = :idUser AND a.tenant_id = :tenantId")
    List<AtendimentoProjectionView> findByUser(@Param("idUser") String idUser, @Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT " +
                    "a.id AS id, " +
                    "a.atendimento_descricao AS descricao," +
                    "a.atendimento_servico AS servicoRaw," +
                    "a.atendimento_valor AS valor," +
                    "a.atendimento_pagamento AS formaPagamento," +
                    "a.atendimento_data AS data," +
                    "a.atendimento_observacao AS observacao," +
                    "a.atendimento_status AS status, " +
                    "a.updated_at AS updatedAt, " +
                    "a.usuario_id AS usuarioId " +
                    "FROM atendimentos a " +
                    "WHERE a.usuario_id = :idUser AND a.id = :idAtendimento AND a.tenant_id = :tenantId")
    Optional<AtendimentoProjectionView> findByUserById(@Param("idUser") String idUser, @Param("idAtendimento") String idAtendimento, @Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT " +
                    "a.id AS id, " +
                    "a.atendimento_descricao AS descricao," +
                    "a.atendimento_servico AS servicoRaw," +
                    "a.atendimento_valor AS valor," +
                    "a.atendimento_pagamento AS formaPagamento," +
                    "a.atendimento_data AS data," +
                    "a.atendimento_observacao AS observacao," +
                    "a.atendimento_status AS status, " +
                    "a.updated_at AS updatedAt," +
                    "a.usuario_id AS usuarioId " +
                    "FROM atendimentos a " +
                    "WHERE a.usuario_id = :id and a.atendimento_data = :data AND a.tenant_id = :tenantId")
    List<AtendimentoProjectionView> findByUserDate(@Param("id") String id, @Param("data") String data, @Param("tenantId") String tenantId);


    @Modifying
    @NativeQuery(
            value = "UPDATE atendimentos a " +
                    "SET a.atendimento_status = 1, a.deleted_at = null " +
                    "WHERE a.id = :id AND a.tenant_id = :tenantId")
    void ativarAtendimento(@Param("id") String id, @Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT " +
                    "a.id AS id, " +
                    "a.atendimento_descricao AS descricao," +
                    "a.atendimento_servico AS servicoRaw," +
                    "a.atendimento_valor AS valor," +
                    "a.atendimento_pagamento AS formaPagamento," +
                    "a.atendimento_data AS data," +
                    "a.atendimento_observacao AS observacao," +
                    "u.usuario_nome AS nomeUsuario," +
                    "a.atendimento_status AS status " +
                    "FROM atendimentos a " +
                    "INNER JOIN usuarios u ON a.usuario_id = u.id " +
                    "WHERE a.tenant_id = :tenantId")
    List<AtendimentoAndUsuarioProjectionView> findAll(@Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT " +
                    "a.id AS id, " +
                    "a.atendimento_descricao AS descricao," +
                    "a.atendimento_servico AS servicoRaw," +
                    "a.atendimento_valor AS valor," +
                    "a.atendimento_pagamento AS formaPagamento," +
                    "a.atendimento_data AS data," +
                    "a.atendimento_observacao AS observacao," +
                    "a.atendimento_status AS status," +
                    "a.updated_at AS updatedAt, " +
                    "a.usuario_id AS usuarioId " +
                    "FROM atendimentos a " +
                    "WHERE a.id = :id AND a.tenant_id = :tenantId")
    Optional<AtendimentoProjectionView> findById(@Param("id") String id, @Param("tenantId") String tenantId);

}
