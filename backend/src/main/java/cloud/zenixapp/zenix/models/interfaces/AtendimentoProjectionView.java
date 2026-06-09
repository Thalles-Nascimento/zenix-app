package cloud.zenixapp.zenix.models.interfaces;


import java.util.List;

public interface AtendimentoProjectionView {
    String getId();
    String getDescricao();
    List<String> getServico();
    Double getValor();
    String getFormaPagamento();
    String getData();
    String getObservacao();
    int getStatus();
}
