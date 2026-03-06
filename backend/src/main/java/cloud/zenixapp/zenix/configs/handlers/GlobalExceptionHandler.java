package cloud.zenixapp.zenix.configs.handlers;

import cloud.zenixapp.zenix.configs.exceptions.*;
import cloud.zenixapp.zenix.models.dtos.responses.ErrorResponseDTO;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"))
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {TokenCreateException.class})
    public ResponseEntity<ErrorResponseDTO> handleTokenCreateException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"))
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {AtendimentoExcluidoException.class})
    public ResponseEntity<ErrorResponseDTO> handleAtendimentoExcluidoException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.GONE.value(),
                ex.getMessage(),
                LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"))
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.GONE);
    }

    @ExceptionHandler(value = {UsuarioExcluidoException.class})
    public ResponseEntity<ErrorResponseDTO> handleUsuarioExcluidoException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.GONE.value(),
                ex.getMessage(),
                LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"))
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.GONE);
    }

    @ExceptionHandler(value = {FilaException.class})
    public ResponseEntity<ErrorResponseDTO> handleFilaException(Exception ex){
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"))
        );

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {UnidadeExcluidoException.class})
    public ResponseEntity<ErrorResponseDTO> handlerUnidadeExcluidoException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.GONE.value(),
                ex.getMessage(),
                LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"))
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.GONE);
    }

    @ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<ErrorResponseDTO> handlerSQLException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"))
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {UnidadeAtivaException.class})
    public ResponseEntity<ErrorResponseDTO> handlerUnidadeAtivaException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                LocalDateTime.now().toInstant(ZoneOffset.of("-03:00"))
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

}
