package cloud.zenixapp.zenix.models.dtos.responses;


import cloud.zenixapp.zenix.models.entities.Planos;

public record SuccessPlanosResponseDTO(
        int status,
        String message

) {
}
