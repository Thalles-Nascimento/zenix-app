package cloud.zenixapp.zenix.configs.error;

import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public final class BindingHandler {

    public static String updateError(BindingResult result){
        Map<String, String> erros = new HashMap<>();
        result.getFieldErrors()
                .forEach(
                        error -> {
                            System.out.println("Campos com erro: " + error.getField());
                            if(!error.getField().equalsIgnoreCase("valor")){
                                erros.put(error.getField(), error.getCode());

                            }});

        Set<Map.Entry<String, String>> dict = erros.entrySet();

        for (Map.Entry<String, String> entry : dict){
            if (entry.getValue().equalsIgnoreCase("NotEmpty") || entry.getValue().equalsIgnoreCase("NotNull")){
                System.out.println("--------------" + entry.getKey().toUpperCase() + "--------------------");
                System.out.println("Dicionario: " + entry);
                System.out.println("Erro: " + entry.getValue());
                String retorno_NotBlank = "O campo não pode ser nulo e/ou vazio!";
                System.out.println("Mensagem Not Blank: " + retorno_NotBlank);
                System.out.println("--------------" + entry.getKey().toUpperCase() + "--------------------");
            }
            else if (entry.getValue().equalsIgnoreCase("Pattern")){
                System.out.println("--------------" + entry.getKey().toUpperCase() + "--------------------");
                System.out.println("Dicionario: " + entry);
                System.out.println("Erro: " + entry.getValue());
                String retorno_Pattern = "O campo está fora do padrão!";
                System.out.println("Mensagem Pattern: " + retorno_Pattern);
                System.out.println("--------------" + entry.getKey().toUpperCase() + "--------------------");
            }
        }

        return "Dicionário: " + erros;
    }

    public static String insertError(BindingResult result){
        return result.getFieldError().getDefaultMessage();
    }
}
