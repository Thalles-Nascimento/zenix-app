package cloud.zenixapp.test.atendimento.repositories;

import cloud.zenixapp.test.atendimento.entities.Atendimento;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtendimentoRepository extends JpaRepository<Atendimento, @NonNull Long> {
}
