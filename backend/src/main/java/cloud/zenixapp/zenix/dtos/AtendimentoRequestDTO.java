package cloud.zenixapp.zenix.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtendimentoRequestDTO {

    @NotEmpty(message = "Campo não pode estar vazio")
    @NotNull(message = "Campo não pode ser nulo!")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ ]+$"
    )
    private String descricao;

    @NotEmpty(message = "Campo não pode estar vazio")
    @NotNull(message = "Campo não pode ser nulo!")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ ]+$"
    )
    private String servico;

    @NotNull(message="Valor não pode ser nulo")
    private Double valor;


}
