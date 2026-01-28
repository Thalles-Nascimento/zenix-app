package cloud.zenixapp.zenix.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtendimentoRequestDTO {

    @NotBlank(message="Descrição não pode ser nula nem em branco")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ ]+$"
    )
    private String descricao;

    @NotBlank(message="Serviço não pode ser nulo nem em branco")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ ]+$"
    )
    private String servico;

    @NotNull(message="Valor não pode ser nulo")
    private Double valor;


}
