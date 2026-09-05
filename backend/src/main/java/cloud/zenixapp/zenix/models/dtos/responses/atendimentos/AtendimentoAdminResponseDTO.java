package cloud.zenixapp.zenix.models.dtos.responses.atendimentos;

import java.util.List;

public record AtendimentoAdminResponseDTO(
        String id,
        String descricao,
        List<String> servico,
        Double valor,
        String formaPagamento,
        String date,
        int status,
        String observacao,
        String barbeiro
) {
}
