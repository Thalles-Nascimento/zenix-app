package cloud.zenixapp.zenix.models.dtos.responses;

public record PlanosResponseDTO(
        Long id,
        String planoDescricao,
        Double valor,
        int limiteAtendimentos
) {
}
