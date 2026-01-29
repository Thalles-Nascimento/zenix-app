package cloud.zenixapp.zenix.configs.error;

import cloud.zenixapp.zenix.dtos.AtendimentoRequestDTO;
import org.springframework.validation.BindingResult;

import java.util.*;

public final class BindingHandler {
    public static Map<String, String> updateError(BindingResult result){
        Map<String, String> erros = new HashMap<>();
        result.getFieldErrors()
                .forEach(
                        error -> {
                            if(!error.getCode().equalsIgnoreCase("NotNull")) {
                                erros.put(error.getField(), error.getDefaultMessage());
                            }});

        return erros;
    }

    public static Map<String, String> insertError(BindingResult result){
        Map<String, String> erros = new HashMap<>();
        result.getFieldErrors()
                .forEach(
                        error -> {
                            erros.put(error.getField(), error.getDefaultMessage());
                        }
                );
        return erros;
    }
}
