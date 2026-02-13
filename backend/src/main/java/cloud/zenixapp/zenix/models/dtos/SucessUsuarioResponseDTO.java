package cloud.zenixapp.zenix.models.dtos;

public record SucessResponseDTO(
        int status,
        String message,
        UsuarioResponseDTO usuario
) {
}
