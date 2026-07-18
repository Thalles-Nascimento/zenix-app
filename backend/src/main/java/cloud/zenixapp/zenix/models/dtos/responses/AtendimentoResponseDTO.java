package cloud.zenixapp.zenix.models.dtos.responses;

import java.util.List;

public record AtendimentoResponseDTO(
        String id,

        String descricao,

        List<String> servico,

        Double valor,

        String formaPagamento,

        String date,

        String observacao,

        int status
){

}
