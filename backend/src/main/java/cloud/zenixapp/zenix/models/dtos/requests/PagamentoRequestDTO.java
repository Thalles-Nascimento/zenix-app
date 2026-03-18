package cloud.zenixapp.zenix.models.dtos.requests;

import jakarta.validation.constraints.NotNull;

public record PagamentoRequestDTO(

        @NotNull(message = "Campo não pode ser nulo!")
        String descricao
) {
}
