package cloud.zenixapp.zenix.models.interfaces;

import java.time.LocalDateTime;

public interface UnidadeSimplesView {
    String getId();
    String getNome();
    String getEndereco();
    LocalDateTime getUpdateAt();
    LocalDateTime getDeleteAt();
    int getStatus();
}
