package cloud.zenixapp.zenix.models.interfaces;

import java.time.LocalDateTime;

public interface ClientesProjectionView {
    String getId();
    String getNome();
    String getEndereco();
    LocalDateTime getUpdateAt();
    LocalDateTime getDeleteAt();
    int getStatus();
}
