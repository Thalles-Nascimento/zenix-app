package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.models.interfaces.AtendimentoProjectionView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.lang.annotation.Native;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public interface AtendimentoRepository extends JpaRepository<Atendimento, String> {

    @Modifying
    @Query(value = "UPDATE Atendimento SET status = -1 WHERE id = :id")
    void deleteLogico(@Param("id") String id);

    @NativeQuery(
            value = "SELECT * FROM atendimentos WHERE usuario_id = :id"
    )
    List<Atendimento> findByUser(@Param("id") String id);

    @NativeQuery(
            value = "SELECT * FROM atendimentos WHERE usuario_id = :idUser and atendimento_id = :id"
    )
    Optional<Atendimento> findByUserById(@Param("idUser") String idUser, @Param("id") String id);

    @NativeQuery(
            value = "SELECT " +
                    "a.id AS id, " +
                    "a.atendimento_descricao AS descricao," +
                    "a.atendimento_servico AS servico," +
                    "a.atendimento_valor AS valor," +
                    "a.atendimento_pagamento AS formaPagamento," +
                    "a.atendimento_data AS 'data'," +
                    "a.atendimento_observacao AS observacao," +
                    "a.atendimento_status AS status " +
                    "FROM atendimentos a " +
                    "WHERE a.usuario_id = :id and a.atendimento_data = :data AND a.tenant_id = :tenantId")
    List<AtendimentoProjectionView> findByUserDate(@Param("id") String id, @Param("data") String data, @Param("tenantId") String tenantId);

    @NativeQuery(
            value = "SELECT * FROM atendimentos WHERE atendimento_id = :id"
    )
    Optional<Atendimento> findByIdAtendimento(@Param("id") String id);

    @Modifying
    @Query(value = "UPDATE Atendimento SET status = 1 WHERE id = :id")
    void ativarAtendimento(@Param("id") String id);

}
