package cloud.zenixapp.zenix.models.interfaces;

import java.time.LocalDate;

public interface ClientesProjectionView {
    String getId();
    String getNome();
    String getTelefone();
    LocalDate getDataRenovacao();
    int getAtendimentoMes();
    int getRetorno();
    int getStatus();
}
