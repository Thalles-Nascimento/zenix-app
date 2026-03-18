package cloud.zenixapp.zenix.models.dtos.responses;


import cloud.zenixapp.zenix.models.entities.Servicos;

public record SuccessServicoResponseDTO(
        int status,
        String message,
        Servicos servicos

) {
}
