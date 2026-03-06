package cloud.zenixapp.zenix.models.dtos.responses;

public record UnidadeResponseDTO(
        Long id,
        String nomeUnidade,
        String endereco,
        Integer status
) {
}
