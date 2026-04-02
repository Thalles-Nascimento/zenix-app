package cloud.zenixapp.zenix.models.dtos.responses;

public record ServicoResponseDTO(
        String id,
        String servico,
        Double valor,
        int status
) {
}
