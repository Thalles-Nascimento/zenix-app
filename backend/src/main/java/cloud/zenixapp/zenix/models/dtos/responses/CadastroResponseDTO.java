package cloud.zenixapp.zenix.models.dtos.responses;

public record CadastroResponseDTO(
        String mensagem,
        String nomeEmpresa,
        String email
) {}