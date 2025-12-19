package cloud.zenixapp.test.zenix.repositories;

import cloud.zenixapp.test.zenix.entities.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {
}
