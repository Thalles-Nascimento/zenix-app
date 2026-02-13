package cloud.zenixapp.zenix.models.dtos;

public record SucessUsuarioResponseDTO(
        int status,
        String message,
        UsuarioResponseDTO usuario
) {
}
