package cloud.zenixapp.zenix.models.interfaces;

import java.time.LocalDateTime;

public interface ClientesSimplesProjectionView {
    String getId();
    String getNome();
    String getTelefone();
    LocalDateTime getUpdatedAt();
    int getStatus();
}
