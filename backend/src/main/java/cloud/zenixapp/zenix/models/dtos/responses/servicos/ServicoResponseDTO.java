package cloud.zenixapp.zenix.models.dtos.responses.servicos;

public record ServicoResponseDTO(
        String id,
        String servico,
        Double valor,
        int status
) {
}
