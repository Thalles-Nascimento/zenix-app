package cloud.zenixapp.zenix.models.dtos.responses;

public record PagamentoResponseDTO(
        Long id,
        String formaPagamento,
        int status
) {
}
