package cloud.zenixapp.zenix.models.dtos.responses.clientes;

import cloud.zenixapp.zenix.models.dtos.responses.planos.PlanosClienteResumoResponseDTO;
import cloud.zenixapp.zenix.models.dtos.responses.telefones.TelefoneClienteResponseDTO;

public record ClienteSimplesResponseDTO(
        String id,
        String nomeCliente,
        TelefoneClienteResponseDTO telefone,
        int status,
        PlanosClienteResumoResponseDTO plano
) {
}
