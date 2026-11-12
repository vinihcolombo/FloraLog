package FloraLog.aep.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void deveTratarRuntimeExceptionComoNotFound() {
        RuntimeException exception =
                new RuntimeException("Recurso não encontrado");

        ResponseEntity<String> resposta =
                handler.tratarRuntimeException(exception);

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
        assertEquals("Recurso não encontrado", resposta.getBody());
    }

    @Test
    void deveTratarIllegalArgumentExceptionComoBadRequest() {
        IllegalArgumentException exception =
                new IllegalArgumentException("Argumento inválido");

        ResponseEntity<String> resposta =
                handler.tratarIllegalArgumentException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        assertEquals("Argumento inválido", resposta.getBody());
    }
}