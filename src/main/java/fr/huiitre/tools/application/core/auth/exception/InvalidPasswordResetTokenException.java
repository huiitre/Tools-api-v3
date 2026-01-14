package fr.huiitre.tools.application.core.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import fr.huiitre.tools.application.common.error.ApplicationException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPasswordResetTokenException extends ApplicationException {

    public InvalidPasswordResetTokenException(String message) {
        super(message);
    }
    
}
