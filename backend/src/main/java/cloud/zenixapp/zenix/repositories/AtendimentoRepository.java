package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.entities.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    @Modifying
    @Query(value = "UPDATE Atendimento SET status = -1 WHERE id = :id")
    void deleteLogico(@Param("id") Long id);

}
