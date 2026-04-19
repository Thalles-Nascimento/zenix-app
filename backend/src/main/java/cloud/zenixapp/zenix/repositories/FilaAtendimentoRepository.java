package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Fila;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface FilaAtendimentoRepository extends JpaRepository<Fila, Long> {

    @Modifying
    @Query(value = "UPDATE Fila SET fimAtendimento = :horaFim WHERE id = :id")
    void marcarHoraFinal(@Param("id") Long id, @Param("horaFim") LocalTime horaFim);

//  Status vai para FINALIZADO
    @Modifying
    @Query(value = "UPDATE Fila SET status = 2 WHERE id = :id")
    void finalizarAtendimentoFila(@Param("id") Long id);

//  Status vai para EM_ATENDIMENTO
    @Modifying
    @Query(value = "UPDATE Fila SET status = 1 WHERE id = :id")
    void paraAtendimento(@Param("id") Long id);

    @Modifying
    @Query(value = "UPDATE Fila SET inicioAtendimento = :horaInicio WHERE id = :id")
    void marcarHoraInicio(@Param("id") Long id, @Param("horaInicio") LocalTime horaInicio);

    @NativeQuery(value = "SELECT * FROM fila_atendimentos WHERE (fila_usuario_id = :id OR fila_usuario_id IS NULL) and fila_status < 2 ORDER BY fila_horario ASC")
    List<Fila> findByUser(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Fila f SET f.usuario.id = :usuarioId WHERE f.id = :id")
    void setarUsuario(@Param("id") Long id, @Param("usuarioId") Long usuarioId);

}
