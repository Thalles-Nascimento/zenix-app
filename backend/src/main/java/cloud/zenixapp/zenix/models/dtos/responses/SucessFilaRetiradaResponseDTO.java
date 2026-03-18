package cloud.zenixapp.zenix.models.dtos.responses;

import cloud.zenixapp.zenix.models.enums.StatusFilaEnum;

import java.util.List;

public record SucessFilaRetiradaResponseDTO(
        int statusHttp,
        String message

) {
}
