package cloud.zenixapp.zenix.models.interfaces;


import java.time.LocalTime;
import java.util.List;

public interface FilaProjectionView {
    String getId();
    String getNomeCliente();
    List<String> getServico();
    String getFormaPagamento();
    LocalTime getHorario();
    int getStatus();
    boolean getSemPreferencia();

}
