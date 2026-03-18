package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Clientes;
import cloud.zenixapp.zenix.models.entities.TelefoneCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Clientes, Long> {

//    TODO Melhorar esse retorno, deixando mais seguro -
//     SELECT c.cliente_id, c.cliente_nome, c.cliente_retorno FROM clientes c WHERE c.telefone_id = :id
    @Query(value = "SELECT * FROM clientes WHERE telefone_id = :id", nativeQuery = true)
    List<Clientes> findClientByNumber(@Param("id") Long id);

    @Query(value = "SELECT * FROM clientes WHERE cliente_nome = :nome", nativeQuery = true)
    Clientes findByName(@Param("nome") String nome);

}
