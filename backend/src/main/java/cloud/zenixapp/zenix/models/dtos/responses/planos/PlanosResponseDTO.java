package cloud.zenixapp.zenix.models.dtos.responses.planos;

import java.util.List;

public record PlanosResponseDTO(
        String id,
        String planoDescricao,
        List<String> servico,
        Double valor,
        int limiteAtendimentos
) {
}
