package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Fila;
import cloud.zenixapp.zenix.models.interfaces.FilaProjectionView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface FilaAtendimentoRepository extends JpaRepository<Fila, String> {

    @Modifying
    @Query(value = "UPDATE Fila SET fimAtendimento = :horaFim WHERE id = :id")
    void marcarHoraFinal(@Param("id") String id, @Param("horaFim") LocalTime horaFim);

//  Status vai para FINALIZADO
    @Modifying
    @Query(value = "UPDATE Fila SET status = 2 WHERE id = :id")
    void finalizarAtendimentoFila(@Param("id") String id);

//  Status vai para EM_ATENDIMENTO
    @Modifying
    @Query(value = "UPDATE Fila SET status = 1 WHERE id = :id")
    void paraAtendimento(@Param("id") String id);

    @Modifying
    @Query(value = "UPDATE Fila SET inicioAtendimento = :horaInicio WHERE id = :id")
    void marcarHoraInicio(@Param("id") String id, @Param("horaInicio") LocalTime horaInicio);

    @NativeQuery(
            value = "SELECT " +
                    "f.id AS id," +
                    "f.fila_client AS nomeCliente," +
                    "f.fila_servico AS servico," +
                    "f.fila_pagamento AS formaPagamento," +
                    "f.fila_horario AS horario," +
                    "f.fila_status AS status," +
                    "f.fila_sem_preferencia AS semPreferencia " +
                    "FROM fila_atendimentos f " +
                    "WHERE (f.fila_usuario_id = :id OR f.fila_usuario_id IS NULL) " +
                    "and f.fila_status < 2 AND f.tenant_id = :tenantId " +
                    "ORDER BY f.fila_horario ASC")
    List<FilaProjectionView> findByUser(@Param("id") String id, @Param("tenantId") String tenantId);

    @Modifying
    @Query("UPDATE Fila f SET f.usuario.id = :usuarioId WHERE f.id = :id")
    void setarUsuario(@Param("id") String id, @Param("usuarioId") String usuarioId);}
