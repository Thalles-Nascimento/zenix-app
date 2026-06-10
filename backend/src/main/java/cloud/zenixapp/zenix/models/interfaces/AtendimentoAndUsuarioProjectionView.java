package cloud.zenixapp.zenix.models.interfaces;


import java.util.List;

public interface AtendimentoAndUsuarioProjectionView {
    String getId();
    String getDescricao();
    List<String> getServico();
    Double getValor();
    String getFormaPagamento();
    String getData();
    String getObservacao();
    String getNomeUsuario();
    int getStatus();
}
