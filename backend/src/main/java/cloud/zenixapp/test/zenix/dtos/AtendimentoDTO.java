package cloud.zenixapp.test.zenix.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class AtendimentoDTO {

    private String descricao;

    private String servico;

    private Double valor;

}
