package cloud.zenixapp.zenix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/${api-url}/health")
@Tag(name = "Health", description = "Endpoint para verificar se a aplicação está rodando")
public class HealthController {


    /*
    *Endpoint para inserção da saúde da aplicação
    *
    */
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
    })
    @Operation(summary = "Checar saúde da aplicação")
    public ResponseEntity<Map<String, String>> checkHealth(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "STATUS", "VIVO! (por enquanto)",
                        "DIAGNÓSTICO", "Nenhum bug detectado nas últimas 2 horas. Suspeito.",
                        "VIBE", "Segurando as pontas com café e oração",
                        "OBSERVAÇÃO", "Se cair, a culpa é do estagiário ou da AWS"
                ));
    }




}
