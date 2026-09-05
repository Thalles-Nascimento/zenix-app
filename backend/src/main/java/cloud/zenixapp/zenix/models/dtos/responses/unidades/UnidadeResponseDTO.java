package cloud.zenixapp.zenix.models.dtos.responses.unidades;

public record UnidadeResponseDTO(
        String id,
        String nomeUnidade,
        String endereco,
        Integer status
) {
}
