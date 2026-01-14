package fr.huiitre.tools.application.core.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import fr.huiitre.tools.application.common.error.ApplicationException;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserDisabledException extends ApplicationException {
    public UserDisabledException(String message) {
        super(message);
    }
}