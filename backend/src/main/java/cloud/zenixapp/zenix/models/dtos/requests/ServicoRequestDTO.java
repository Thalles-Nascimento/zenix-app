package cloud.zenixapp.zenix.models.dtos.requests;

import jakarta.validation.constraints.NotNull;

public record ServicoRequestDTO(

        @NotNull(message = "Campo não pode ser nulo!")
        String servico,

        @NotNull(message="Valor não pode ser nulo")
        Double valor
) {
}
