package cloud.zenixapp.zenix.models.dtos.responses;


public record SuccessServicoResponseDTO(
        int status,
        String message,
        ServicoResponseDTO servicosDTO

) {
}
