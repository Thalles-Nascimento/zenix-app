package cloud.zenixapp.zenix.models.dtos.requests;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PlanosRequestDTO(

        @NotNull(message = "Campo não pode ser nulo!")
        String planoDescricao,

        @NotNull(message = "Campo não pode ser nulo!")
        Double valor,

        @NotNull(message = "Campo não pode ser nulo!")
        List<String> servico,

        @NotNull(message = "Campo não pode ser nulo!")
        int limiteAtendimentos
) {
}
