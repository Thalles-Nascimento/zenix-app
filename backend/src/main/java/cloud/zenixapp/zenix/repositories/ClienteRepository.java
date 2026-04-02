package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Clientes;
import cloud.zenixapp.zenix.models.entities.TelefoneCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Clientes, String> {

//    TODO Melhorar esse retorno, deixando mais seguro -
//     SELECT c.cliente_id, c.cliente_nome, c.cliente_retorno FROM clientes c WHERE c.telefone_id = :id
    @Query(value = "SELECT * FROM clientes WHERE telefone_id = :id", nativeQuery = true)
    List<Clientes> findClientByNumber(@Param("id") String id);

    @Query(value = "SELECT * FROM clientes WHERE cliente_nome = :nome", nativeQuery = true)
    Clientes findByName(@Param("nome") String nome);

    @Query(value = "SELECT * FROM clientes WHERE cliente_nome LIKE %:nome% AND cliente_status = 1", nativeQuery = true)
    List<Clientes> findByNameContaining(@Param("nome") String nome);

    @Modifying
    @Query("UPDATE Clientes SET atendimentosMes = 0 WHERE planos IS NOT NULL AND DAY(dataRenovacao) = :dia")
    void resetarAtendimentosMes(@Param("dia") int dia);

    @Modifying
    @Query(value = "UPDATE Clientes SET status = -1 WHERE id = :id")
    void deleteLogico(@Param("id") String id);

    @Modifying
    @Query(value = "UPDATE Clientes SET status = 1 WHERE id = :id")
    void ativarCliente(@Param("id") String id);

}
