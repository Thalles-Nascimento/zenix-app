package cloud.zenixapp.zenix.dtos;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtendimentoRequestDTO {

    @NotNull(message = "Campo não pode ser nulo!")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ ]+$",
            message = "O valor não está respeitando o padrão"
    )
    private String descricao;

    @NotNull(message = "Campo não pode ser nulo!")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ ]+$",
            message = "O valor não está respeitando o padrão"
    )
    private String servico;

    @NotNull(message="Valor não pode ser nulo")
    private Double valor;


}
