package cloud.zenixapp.zenix.models.interfaces;

import java.time.LocalDateTime;

public interface ServicosView {
    String getId();
    String getServico();
    Double getValor();
    LocalDateTime getUpdateAt();
    LocalDateTime getDeleteAt();
    int getStatus();

}
