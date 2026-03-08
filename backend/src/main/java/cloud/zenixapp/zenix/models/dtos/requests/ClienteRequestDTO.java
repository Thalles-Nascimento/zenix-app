package cloud.zenixapp.zenix.models.dtos.requests;

import cloud.zenixapp.zenix.models.entities.TelefoneCliente;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ClienteRequestDTO(
        @NotNull(message = "Campo não pode ser nulo!")
        @Pattern(
                regexp = "^[A-Za-zÀ-ÿ ]+$",
                message = "O valor não está respeitando o padrão"
        )
        String nomeCliente,

        String telefoneCliente
) {
}
