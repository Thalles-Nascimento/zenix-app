package cloud.zenixapp.zenix.models.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AtendimentoResponseDTO(
        Long id,

        String descricao,

        String servico,

        Double valor,

        String date,

        int status
){

}
