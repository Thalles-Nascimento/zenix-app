package cloud.zenixapp.zenix.models.interfaces;

public interface UsuariosProjectionView {
    String getId();
    String getNome();
    String getEmail();
    String getCpf();
    String getGrupo();
    String getUnidadeId();
    String getUnidadeNome();
    String getUnidadeEndereco();
    int getUnidadeStatus();
    int getStatus();

}
