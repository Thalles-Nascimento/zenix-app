package cloud.zenixapp.zenix.models.dtos.responses;

public record ServicoResponseDTO(
        Long id,
        String servico,
        Double valor,
        int status
) {
}
