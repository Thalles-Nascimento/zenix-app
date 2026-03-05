package cloud.zenixapp.zenix.models.dtos.responses;


import cloud.zenixapp.zenix.models.entities.Unidades;

public record SuccessUnidadeDeleteResponseDTO(
        int status,
        String message,
        String nomeUnidade

) {
}
