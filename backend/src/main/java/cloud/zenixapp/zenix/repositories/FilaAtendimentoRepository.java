package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.models.entities.Fila;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilaAtendimentoRepository extends JpaRepository<Fila, Long> {

//  Status vai para FINALIZADO
    @Modifying
    @Query(value = "UPDATE Fila SET status = 2 WHERE id = :id")
    void finalizarAtendimentoFila(@Param("id") Long id);

//  Status vai para EM_ATENDIMENTO
    @Modifying
    @Query(value = "UPDATE Fila SET status = 1 WHERE id = :id")
    void paraAtendimento(@Param("id") Long id);

    @Query(value = "SELECT * FROM `fila-atendimentos` WHERE fila_usuario_id = :id AND fila_status != 2", nativeQuery = true)
    List<Fila> findByUser(@Param("id") Long id);

}
