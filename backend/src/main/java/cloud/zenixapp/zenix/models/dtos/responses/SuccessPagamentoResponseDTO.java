package cloud.zenixapp.zenix.models.dtos.responses;


import cloud.zenixapp.zenix.models.entities.FormaPagamento;

public record SuccessPagamentoResponseDTO(
        int status,
        String message,
        FormaPagamento pagamento

) {
}
