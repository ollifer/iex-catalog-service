package iex.rest.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@Slf4j

//todo: add others ex. handlers
public class CommonExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> serverError(Exception ex) {

        log.error("Internal error: ", ex);
        return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR.name(), INTERNAL_SERVER_ERROR.getReasonPhrase()),
                INTERNAL_SERVER_ERROR);
    }
}
