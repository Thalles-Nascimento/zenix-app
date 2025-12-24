package cloud.zenixapp.test.zenix.repositories;

import cloud.zenixapp.test.zenix.entities.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

//    @Modifying
//    @Transactional
//    @Query("DELETE FROM Atendimento a WHERE a.id = :id")
//    int deleteAtendimento(@Param("id") Long id);


}
