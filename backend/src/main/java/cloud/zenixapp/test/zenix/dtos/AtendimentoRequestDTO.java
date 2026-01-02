package cloud.zenixapp.test.zenix.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtendimentoRequestDTO {

    @NotNull(message = "A descrição não pode ser Nula/Vazia")
    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotNull(message = "O serviço não pode ser Nulo/Vazio")
    @NotBlank(message = "O serviço é obrigatório")
    private String servico;

    @NotNull(message = "O valor não pode ser Nulo/Vazio")
    @NotBlank(message = "O valor é obrigatório")
    private Double valor;

}
