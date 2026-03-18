package cloud.zenixapp.zenix.models.dtos.responses;

public record ClienteResponseDTO(
        Long id,
        String nomeCliente,
        String telefone,
        int vezesRetorno,
        int atendimentosMes,
        PlanosResponseDTO plano
) {
}
