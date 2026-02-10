package cloud.zenixapp.zenix.models.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AtendimentoResponseDTO(
        String descricao,

        String servico,

        Double valor,

        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDateTime date,

        int status){

}
