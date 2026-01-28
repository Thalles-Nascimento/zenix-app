package cloud.zenixapp.zenix.configs.error;

import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class BindingHandler {

    public static String updateError(BindingResult result){
        Map<String, String> erros = new HashMap<>();
        result.getFieldErrors()
                .forEach(
                        error -> {
                            if(!error.getField().equalsIgnoreCase("valor")){
                                erros.put(error.getField(), error.getCode());

                            }});

        Set<Map.Entry<String, String>> dict = erros.entrySet();

        for (Map.Entry<String, String> entry : dict){
            if (entry.getValue().equalsIgnoreCase("Pattern")){
                return "Não foi possível atualizar! Descrição ou Serviço contendo dígitos";
            }
            else if (entry.getValue().equalsIgnoreCase("NotBlank")){
                if (result.getFieldError(entry.getKey()).getRejectedValue() == null){
                    return "Ok";
                }
                else{
                    return "Não foi possível atualizar! Descrição ou Serviço está vazio!";
                }
            }
        }

        return "Dicionário: " + erros;
    }

    public static String insertError(BindingResult result){
        return result.getFieldError().getDefaultMessage();
    }
}
