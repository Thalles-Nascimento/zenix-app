package cloud.zenixapp.zenix.models.dtos.responses;

public record SucessUsuarioResponseDTO(
        int status,
        String message,
        UsuarioResponseDTO usuario
) {
}
