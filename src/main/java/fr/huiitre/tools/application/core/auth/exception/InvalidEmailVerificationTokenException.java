package fr.huiitre.tools.application.core.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import fr.huiitre.tools.application.common.error.ApplicationException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEmailVerificationTokenException extends ApplicationException {

    public InvalidEmailVerificationTokenException() {
        super("EMAIL_VERIFICATION_INVALID_OR_EXPIRED");
    }
}