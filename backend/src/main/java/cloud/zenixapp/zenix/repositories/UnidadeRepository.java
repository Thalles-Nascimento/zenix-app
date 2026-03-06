package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Unidades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UnidadeRepository extends JpaRepository<Unidades, Long> {

    @Modifying
    @Query(value = "UPDATE Unidades SET status = -1 WHERE id = :id")
    void deleteLogico(@Param("id") Long id);

    @Modifying
    @Query(value = "UPDATE Unidades SET status = 1 WHERE id = :id")
    void ativarUnidade(@Param("id") Long id);
}
