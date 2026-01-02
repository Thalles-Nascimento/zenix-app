package cloud.zenixapp.zenix.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtendimentoUpdateRequestDTO {

    private String descricao;

    private String servico;

    private Double valor;

}
