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

    @Query(value = "SELECT * FROM atendimentos WHERE usuario_id = :id", nativeQuery = true)
    List<Atendimento> findByUser(@Param("id") Long id);

    @Query(value = "SELECT * FROM atendimentos WHERE usuario_id = :idUser and atendimento_id = :id", nativeQuery = true)
    Optional<Atendimento> findByUserById(@Param("idUser") Long idUser, @Param("id") Long id);

    @Query(value = "SELECT * FROM atendimentos WHERE usuario_id = :id and atendimento_data = :data", nativeQuery = true)
    List<Atendimento> findByUserDate(@Param("id") Long id, @Param("data") String data);

    @Query(value = "SELECT * FROM atendimentos WHERE atendimento_id = :id", nativeQuery = true)
    Optional<Atendimento> findByIdAtendimento(@Param("id") Long id);

    @Modifying
    @Query(value = "UPDATE Atendimento SET status = 1 WHERE id = :id")
    void ativarAtendimento(@Param("id") Long id);

}
