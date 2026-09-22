package cloud.zenixapp.zenix.repositories;

import cloud.zenixapp.zenix.models.dtos.requests.AtendimentoRequestDTO;
import cloud.zenixapp.zenix.models.entities.Atendimento;
import cloud.zenixapp.zenix.models.entities.Usuarios;
import cloud.zenixapp.zenix.models.enums.UsuariosRoleEnum;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AtendimentoRepositoryTest {

    @Autowired
    AtendimentoRepository atendimentoRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Atendimento encontrado, retornando True para isPresent")
    void findByIdPresent() {
        List<String> servicos = new ArrayList<>();
        servicos.add("Corte");
        servicos.add("Barba");
        AtendimentoRequestDTO data = new AtendimentoRequestDTO(
                "Thalles Tests",
                servicos,
                "PIX",
                null,
                45.0
        );
        this.createAtendimento(data);
        Optional<Atendimento> result = this.atendimentoRepository.findById("id-atendimento-novo");

        assertThat(result.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Atendimento não existe, retornando True para isEmpty")
    void findByIdEmpty() {

        Optional<Atendimento> result = this.atendimentoRepository.findById("id-atendimento-novo");

        assertThat(result.isEmpty()).isTrue();
    }

    private Atendimento createAtendimento(AtendimentoRequestDTO data){
        Atendimento newAtendimento = new Atendimento();
        newAtendimento.setId("id-atendimento-novo");
        newAtendimento.setDescricao(data.descricao());
        newAtendimento.setValor(data.valor());
        newAtendimento.setDate("10/09/2026");
        newAtendimento.setFormaPagamento(data.formaPagamento());
        Usuarios user = new Usuarios(
                "Matheus Gomes",
                "matheus@gmail.com",
                "123456",
                "22255544425",
                UsuariosRoleEnum.ADMIN

        );

        this.entityManager.persist(user);
        newAtendimento.setUsuarios(user);
        newAtendimento.setObservacao(data.observacao());
        newAtendimento.setTenant("id-tenant-tests");
        this.entityManager.persist(newAtendimento);

        return newAtendimento;

    }
}