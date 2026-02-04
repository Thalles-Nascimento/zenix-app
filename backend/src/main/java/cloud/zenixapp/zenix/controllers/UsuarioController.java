package cloud.zenixapp.zenix.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/usuarios")
@Tag(name = "Usuário", description = "Endpoints do serviço de Usuários")
public class UsuarioController {

    @GetMapping
    public ResponseEntity<String> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body("Sucesso!");
    }

}
