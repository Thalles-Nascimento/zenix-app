package cloud.zenixapp.zenix.models.enums;

import lombok.Getter;

@Getter
public enum UsuariosRoleEnum {
    
    ADMIN("admin"),
    USER("user");

    private String grupo;

    UsuariosRoleEnum(String grupo) {
        this.grupo = grupo;
    }

}
