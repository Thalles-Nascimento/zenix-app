package cloud.zenixapp.zenix.models.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ClienteUpdateRequestDTO(
        @NotNull(message = "Campo não pode ser nulo!")
        @Pattern(
                regexp = "^[A-Za-zÀ-ÿ ]+$",
                message = "O valor não está respeitando o padrão"
        )
        String nomeCliente,

        String telefoneCliente
) {
}
