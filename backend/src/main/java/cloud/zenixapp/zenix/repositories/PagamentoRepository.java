package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.entities.FormaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<FormaPagamento, Long> {

}
