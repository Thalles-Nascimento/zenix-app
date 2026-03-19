package cloud.zenixapp.zenix.models.dtos.responses;

import java.util.List;

public record PlanosResponseDTO(
        Long id,
        String planoDescricao,
        List<String> servico,
        Double valor,
        int limiteAtendimentos
) {
}
