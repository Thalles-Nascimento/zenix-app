package cloud.zenixapp.zenix.models.dtos.responses;


import cloud.zenixapp.zenix.models.entities.Unidades;

public record SuccessUnidadeResponseDTO(
        int status,
        String message,
        Unidades unidade

) {
}
