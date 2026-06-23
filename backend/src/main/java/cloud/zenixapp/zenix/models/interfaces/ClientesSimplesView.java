package cloud.zenixapp.zenix.models.interfaces;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ClientesSimplesView {
    String getId();
    String getNome();
    String getTelefone();
    LocalDate getDataRenovacao();
    LocalDateTime getUpdatedAt();
    int getAtendimentoMes();
    int getRetorno();
    int getStatus();
}
