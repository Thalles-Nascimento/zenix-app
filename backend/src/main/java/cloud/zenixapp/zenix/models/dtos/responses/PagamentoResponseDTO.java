package cloud.zenixapp.zenix.models.dtos.responses;

public record PagamentoResponseDTO(
        String id,
        String formaPagamento,
        int status
) {
}
