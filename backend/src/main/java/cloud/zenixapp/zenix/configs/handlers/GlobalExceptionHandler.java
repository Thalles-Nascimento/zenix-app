package cloud.zenixapp.zenix.configs.handlers;

import cloud.zenixapp.zenix.configs.exceptions.*;
import cloud.zenixapp.zenix.models.dtos.responses.ErrorResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("-03:00");
    private static final ZoneId TIME_ZONE = ZoneId.of("America/Sao_Paulo");

    /*=====================================================================================
     * Handler para exceções em que a entidade não foi encontrada.
     *======================================================================================*/
    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now(TIME_ZONE).toInstant(ZONE_OFFSET)
        );
        log.error("Problemas para encontrar o objeto: [Status: {}] => [Message: {}]", errorResponse.status(), errorResponse.message());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /*=====================================================================================
     * Handler para exceções em que há erro na criação do 'token'.
     *======================================================================================*/
    @ExceptionHandler(value = {TokenCreateException.class})
    public ResponseEntity<ErrorResponseDTO> handleTokenCreateException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                LocalDateTime.now(TIME_ZONE).toInstant(ZONE_OFFSET)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*=====================================================================================
    * Handler para exceções em que a entidade já foi excluída - status = −1
    *======================================================================================*/
    @ExceptionHandler(value = {ExcluidoException.class})
    public ResponseEntity<ErrorResponseDTO> handleExcluidoException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.GONE.value(),
                ex.getMessage(),
                LocalDateTime.now(TIME_ZONE).toInstant(ZONE_OFFSET)
        );
        log.error("Problemas para fazer a exclusão: [Status: {}] => [Message: {}]", errorResponse.status(), errorResponse.message());
        return new ResponseEntity<>(errorResponse, HttpStatus.GONE);

    }

    /*=====================================================================================
     * Handler para exceções em que a entidade já está ativada - status = 1.
     *======================================================================================*/
    @ExceptionHandler(value = {AtivoException.class})
    public ResponseEntity<ErrorResponseDTO> handleAtivoException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.GONE.value(),
                ex.getMessage(),
                LocalDateTime.now(TIME_ZONE).toInstant(ZONE_OFFSET)
        );
        log.error("Problemas para fazer a ativação: [Status: {}] => [Message: {}]", errorResponse.status(), errorResponse.message());
        return new ResponseEntity<>(errorResponse, HttpStatus.GONE);

    }

    /*=====================================================================================
     * Handler para exceções em que há conflito.
     *======================================================================================*/
    @ExceptionHandler(value = {ConflictException.class})
    public ResponseEntity<ErrorResponseDTO> handleConflictException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now(TIME_ZONE).toInstant(ZONE_OFFSET)
        );
        log.error("Problemas de conflito: [Status: {}] => [Message: {}]", errorResponse.status(), errorResponse.message());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

}
