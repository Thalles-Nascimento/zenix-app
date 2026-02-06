package cloud.zenixapp.zenix.entities.enums;

public enum UsuariosRoleEnum {
    
    ADMIN("admin"),
    USER("user");

    private String grupo;

    UsuariosRoleEnum(String grupo) {
        this.grupo = grupo;
    }

    public String getGrupo(){
        return this.grupo;
    }
}
