package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.models.entities.Fila;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilaAtendimentoRepository extends JpaRepository<Fila, Long> {

    @Modifying
    @Query(value = "UPDATE Fila SET status = 2 WHERE id = :id")
    void finalizarAtendimentoFila(@Param("id") Long id);

    @Modifying
    @Query(value = "UPDATE Fila SET status = 1 WHERE id = :id")
    void paraAtendimento(@Param("id") Long id);

    @Query(value = "SELECT * FROM `fila-atendimentos` WHERE fila_usuario_id = :id", nativeQuery = true)
    List<Fila> findByUser(@Param("id") Long id);

}
