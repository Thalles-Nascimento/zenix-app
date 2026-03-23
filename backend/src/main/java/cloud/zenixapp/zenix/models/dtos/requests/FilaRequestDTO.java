package cloud.zenixapp.zenix.models.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record FilaRequestDTO(

        @NotNull(message = "Campo não pode ser nulo!")
        @Pattern(
                regexp = "^[A-Za-zÀ-ÿ ]+$",
                message = "O valor não está respeitando o padrão"
        )
        String nomeCliente,

//        TODO Criar uma tabela serviço
        @NotNull(message = "Campo não pode ser nulo!")
        List<String> servico,

//      TODO Verificar a possibilidade de um relacionamento entre telefoneCliente e Fila
        String telefoneCliente,

        @NotNull(message = "Campo não pode ser nulo!")
        @Pattern(
                regexp = "^[A-Za-zÀ-ÿ ]+$",
                message = "O valor não está respeitando o padrão"
        )
        String formaPagamento,

        Long idBarbeiro,

        Long idUnidade,

        boolean semPreferencia
) {
}
