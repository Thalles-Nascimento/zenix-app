package cloud.zenixapp.zenix.models.dtos.requests;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record AtendimentoRequestDTO (
        @NotNull(message = "Campo não pode ser nulo!")
        @Pattern(
                regexp = "^[A-Za-zÀ-ÿ ]+$",
                message = "O valor não está respeitando o padrão"
        )
        String descricao,

        @NotNull(message = "Campo não pode ser nulo!")
        @Pattern(
                regexp = "^[\\p{L} +]+$",
                message = "O valor não está respeitando o padrão"
        )
        String servico,

        @NotNull(message = "Campo não pode ser nulo!")
        @Pattern(
                regexp = "^[A-Za-zÀ-ÿ ]+$",
                message = "O valor não está respeitando o padrão"
        )
        String formaPagamento,

        @NotNull(message="Valor não pode ser nulo")
        Double valor
){

}
