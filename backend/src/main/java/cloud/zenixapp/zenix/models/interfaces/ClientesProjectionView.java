package cloud.zenixapp.zenix.models.interfaces;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ClientesProjectionView {
    String getId();
    String getNome();
    String getTelefone();
    String getPlanosId();
    LocalDate getDataRenovacao();
    LocalDateTime getUpdatedAt();
    int getAtendimentoMes();
    int getRetorno();
    int getStatus();
}
