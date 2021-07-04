package team.natlex.NatLex.exceptions.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import team.natlex.NatLex.exceptions.*;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        if (ex instanceof SectionNotFoundException)
            return new ResponseEntity<>("Section not found", new HttpHeaders(), HttpStatus.NOT_FOUND);
        if (ex instanceof ClassAlreadyExistsException)
            return new ResponseEntity<>("Class already exist", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        if (ex instanceof ExportStillInProgressException)
            return new ResponseEntity<>("Export still in progress", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        if (ex instanceof GeoClassNotFoundException)
            return new ResponseEntity<>("Class not found", new HttpHeaders(), HttpStatus.NOT_FOUND);
        if (ex instanceof WrongInputException)
            return new ResponseEntity<>("Wrong input", new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE);
        if (ex instanceof ImportErrorException)
            return new ResponseEntity<>("Something went wrong during import", new HttpHeaders(), HttpStatus.CONFLICT);
        return new ResponseEntity<>("Other error", HttpStatus.I_AM_A_TEAPOT);
    }
}