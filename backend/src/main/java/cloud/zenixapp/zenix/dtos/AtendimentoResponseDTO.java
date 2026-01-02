package cloud.zenixapp.zenix.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtendimentoResponseDTO {

    private String descricao;

    private String servico;

    private Double valor;

    private LocalDateTime date;

    private int status;

}
