package cloud.zenixapp.zenix.models.dtos.responses;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

/**
 * <h2>
 *     Record ErrorResponseDTO
 * </h2>
 * <p>
 *     DTO padrão para respostas de erros e exceções.
 * </p>
 * @param status Status Code HTTP.
 * @param message Mensagem de erro/exceção.
 * @param instant Instante em que ocorreu.
 * @see org.springframework.http.HttpStatus
 * @see JsonFormat
 * @see Instant
 */
public record ErrorResponseDTO(
        int status,
        String message,
        @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
        Instant instant
) {
}
