package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.Servicos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServicoRepository extends JpaRepository<Servicos, String> {

}
