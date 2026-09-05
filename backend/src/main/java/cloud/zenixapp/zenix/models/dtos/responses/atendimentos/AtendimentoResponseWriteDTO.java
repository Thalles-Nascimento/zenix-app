package cloud.zenixapp.zenix.models.dtos.responses.atendimentos;

import java.time.LocalDateTime;
import java.util.List;

public record AtendimentoResponseWriteDTO(
        String id,

        String descricao,

        List<String> servico,

        Double valor,

        String formaPagamento,

        String date,

        LocalDateTime updatedAt,

        LocalDateTime deletedAt,

        String observacao,

        int status
){

}
