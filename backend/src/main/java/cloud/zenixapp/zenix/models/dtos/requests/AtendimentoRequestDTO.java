package cloud.zenixapp.zenix.models.dtos.requests;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

/**
 * <h2>
 *     Record AtendimentoRequestDTO
 * </h2>
 * <p>
 *     DTO para requisições do Domínio Atendimento.
 * </p>
 * @param descricao Nome do cliente do atendimento {@link NotNull} {@link Pattern}
 * @param servico Lista de serviços do atendimento {@link NotNull}
 * @param formaPagamento Forma de pagamento do atendimento {@link NotNull} {@link Pattern}
 * @param observacao Observação do atendimento {@code Opcional}
 * @param valor Valor total do atendimento {@link NotNull}
 */
public record AtendimentoRequestDTO (
        @NotNull(message = "Campo não pode ser nulo!")
        @Pattern(
                regexp = "^[A-Za-zÀ-ÿ ]+$",
                message = "O valor não está respeitando o padrão"
        )
        String descricao,

        @NotNull(message = "Campo não pode ser nulo!")
        List<String> servico,

        @NotNull(message = "Campo não pode ser nulo!")
        @Pattern(
                regexp = "^[A-Za-zÀ-ÿ ]+$",
                message = "O valor não está respeitando o padrão"
        )
        String formaPagamento,

        String observacao,

        @NotNull(message="Valor não pode ser nulo")
        Double valor
){

}
