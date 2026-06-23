package cloud.zenixapp.zenix.models.interfaces;

import java.time.LocalDate;
import java.util.List;

public interface TelefoneProjectionView {
    String getId();
    String getNome();
    String getTelefone();
    String getPlanoId();
    String getPlanoDescricao();
    Double getValor();
    List<String> getServico();
    int getLimiteAtendimentos();
    LocalDate getDataRenovacao();
    int getAtendimentoMes();
    int getRetorno();
    int getStatus();
}
