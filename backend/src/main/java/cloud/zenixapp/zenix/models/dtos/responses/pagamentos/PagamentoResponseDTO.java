package cloud.zenixapp.zenix.models.dtos.responses.pagamentos;

public record PagamentoResponseDTO(
        String id,
        String formaPagamento,
        int status
) {
}
