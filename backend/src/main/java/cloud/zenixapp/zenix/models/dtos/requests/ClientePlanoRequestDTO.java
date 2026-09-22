package cloud.zenixapp.zenix.models.dtos.requests;

import jakarta.validation.constraints.NotNull;

public record ClientePlanoRequestDTO(


        @NotNull(message = "Campo não pode ser nulo!")
        String idPlano

) {
}
