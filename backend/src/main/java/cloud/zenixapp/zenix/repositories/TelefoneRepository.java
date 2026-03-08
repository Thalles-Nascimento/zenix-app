package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.TelefoneCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TelefoneRepository extends JpaRepository<TelefoneCliente, Long> {

    @Query(value = "SELECT * FROM telefones_clientes WHERE telefone_cliente = :number", nativeQuery = true)
    Optional<TelefoneCliente> findByNumber(@Param("number") String number);

}
