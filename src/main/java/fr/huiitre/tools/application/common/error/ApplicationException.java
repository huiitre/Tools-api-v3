package fr.huiitre.tools.application.common.error;

public abstract class ApplicationException extends RuntimeException {

    protected ApplicationException(String code) {
        super(code);
    }
}
