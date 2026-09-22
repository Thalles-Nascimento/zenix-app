package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Fila;
import cloud.zenixapp.zenix.models.interfaces.FilaProjectionView;
import cloud.zenixapp.zenix.models.interfaces.FilaSimplesView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface FilaAtendimentoRepository extends JpaRepository<Fila, String> {

//  Status vai para FINALIZADO
    @Modifying
    @NativeQuery(value = "UPDATE fila_atendimentos SET fila_status = 2, fila_final_atendimento = :horaFim WHERE id = :id AND tenant_id = :tenantId")
    void finalizarAtendimentoFila(@Param("id") String id, @Param("tenantId") String tenantId, @Param("horaFim") LocalTime horaFim);

//  Status vai para EM_ATENDIMENTO
    @Modifying
    @NativeQuery(value = "UPDATE fila_atendimentos SET fila_status = 1, fila_inicio_atendimento = :horaInicio WHERE id = :id AND tenant_id = :tenantId")
    void paraAtendimento(@Param("id") String id, @Param("tenantId") String tenantId, @Param("horaInicio") LocalTime horaInicio);

    @Modifying
    @NativeQuery("UPDATE fila_atendimentos SET fila_usuario_id = :usuarioId WHERE id = :id AND tenant_id = :tenantId")
    void setarUsuario(@Param("id") String id, @Param("tenantId") String tenantId, @Param("usuarioId") String usuarioId);

    @NativeQuery(
            value = "SELECT " +
                    "f.id AS id," +
                    "f.fila_client AS nomeCliente," +
                    "f.fila_servico AS servicoRaw," +
                    "f.fila_pagamento AS formaPagamento," +
                    "f.fila_horario AS horario," +
                    "f.fila_status AS status," +
                    "f.fila_sem_preferencia AS semPreferencia " +
                    "FROM fila_atendimentos f " +
                    "WHERE (f.fila_usuario_id = :id OR f.fila_usuario_id IS NULL) " +
                    "and f.fila_status < 2 AND f.tenant_id = :tenantId " +
                    "ORDER BY f.fila_horario ASC")
    List<FilaProjectionView> findByUser(@Param("id") String id, @Param("tenantId") String tenantId);


    @NativeQuery(
            value = "SELECT " +
                    "f.id AS id," +
                    "f.fila_client AS nomeCliente," +
                    "f.fila_status AS status," +
                    "f.fila_sem_preferencia AS semPreferencia " +
                    "FROM fila_atendimentos f " +
                    "WHERE f.id = :id AND f.tenant_id = :tenantId")
    Optional<FilaSimplesView> findById(@Param("id") String id, @Param("tenantId") String tenantId);

}
