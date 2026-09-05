package cloud.zenixapp.zenix.models.dtos.responses.usuarios;

public record CadastroResponseDTO(
        String mensagem,
        String nomeEmpresa,
        String email
) {}