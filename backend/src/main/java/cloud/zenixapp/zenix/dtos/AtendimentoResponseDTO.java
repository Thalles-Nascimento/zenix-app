package cloud.zenixapp.zenix.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime date;

    private int status;

}
