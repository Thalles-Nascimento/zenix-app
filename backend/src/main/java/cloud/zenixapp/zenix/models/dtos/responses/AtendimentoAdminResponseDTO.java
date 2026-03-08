package cloud.zenixapp.zenix.models.dtos.responses;

import java.util.List;

public record AtendimentoAdminResponseDTO(
        Long id,
        String descricao,
        List<String> servico,
        Double valor,
        String formaPagamento,
        String date,
        int status,
        String barbeiro
) {
}
