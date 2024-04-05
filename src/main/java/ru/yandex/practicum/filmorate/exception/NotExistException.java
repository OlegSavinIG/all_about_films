package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotExistException extends ResponseStatusException {
    public NotExistException(HttpStatus status) {
        super(status);
    }



    public NotExistException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public NotExistException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    public NotExistException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
    }
}
