package cloud.zenixapp.zenix.models.dtos.requests;

import jakarta.validation.constraints.NotNull;

public record PlanosRequestDTO(

        @NotNull(message = "Campo não pode ser nulo!")
        String planoDescricao,

        @NotNull(message = "Campo não pode ser nulo!")
        Double valor,

        @NotNull(message = "Campo não pode ser nulo!")
        int limiteAtendimentos
) {
}
