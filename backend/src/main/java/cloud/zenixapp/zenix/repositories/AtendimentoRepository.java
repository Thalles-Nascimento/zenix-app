package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    @Modifying
    @Query(value = "UPDATE Atendimento SET status = -1 WHERE id = :id")
    void deleteLogico(@Param("id") Long id);

    @Query("SELECT a FROM Atendimento a WHERE a.date = :date")
    List<Atendimento> findAllByDate(@Param("date") String date);

    @Query(value = "SELECT * FROM atendimento WHERE usuario_id = :id", nativeQuery = true)
    List<Atendimento> findByUser(@Param("id") Long id);

    @Query(value = "SELECT SUM(a.atendimento_valor) FROM atendimento a WHERE a.usuario_id = :id and a.atendimento_status = 1", nativeQuery = true)
    Double sumAtendimentos(@Param("id") Long id);

    @Query(value = "SELECT * FROM atendimento WHERE usuario_id = :idUser and atendimento_id = :id", nativeQuery = true)
    Optional<Atendimento> findByUserById(@Param("idUser") Long idUser, @Param("id") Long id);

    @Query(value = "SELECT * FROM atendimento WHERE usuario_id = :id and atendimento_data = :data", nativeQuery = true)
    List<Atendimento> findByUserDate(@Param("id") Long id, @Param("data") String data);

}
