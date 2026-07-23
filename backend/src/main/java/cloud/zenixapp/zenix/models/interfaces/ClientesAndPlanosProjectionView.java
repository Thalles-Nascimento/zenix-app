package cloud.zenixapp.zenix.models.interfaces;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ClientesAndPlanosProjectionView {
    String getId();
    String getNome();
    String getTelefone();
    String getPlanosId();
    String getDescricao();
    List<String> getServicos();
    Double getValor();
    int getAtendimentos();
    LocalDate getDataRenovacao();
    LocalDateTime getUpdatedAt();
    int getAtendimentoMes();
    int getRetorno();
    int getStatus();
}
