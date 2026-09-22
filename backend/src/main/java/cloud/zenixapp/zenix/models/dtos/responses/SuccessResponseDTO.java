package cloud.zenixapp.zenix.models.dtos.responses;

/**
 * <h2>
 *     Record SuccessResponseDTO
 * </h2>
 * <p>
 *     DTO padrão para respostas de sucesso.
 * </p>
 * @see org.springframework.http.HttpStatus
 * @param status Status Code HTTP.
 * @param message Mensagem de sucesso.
 */
public record SuccessResponseDTO(
        int status,
        String message
) {
}
