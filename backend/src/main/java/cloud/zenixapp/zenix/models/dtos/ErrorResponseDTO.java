package cloud.zenixapp.zenix.models.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public record ErrorResponseDTO(
        int status,
        String message,
        @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="UTC")
        Instant instant
) {
}
