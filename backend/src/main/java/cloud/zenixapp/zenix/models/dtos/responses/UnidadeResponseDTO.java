package cloud.zenixapp.zenix.models.dtos.responses;

public record UnidadeResponseDTO(
        String id,
        String nomeUnidade,
        String endereco,
        Integer status
) {
}
