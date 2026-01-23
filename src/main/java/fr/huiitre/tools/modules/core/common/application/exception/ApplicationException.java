package fr.huiitre.tools.modules.core.common.application.exception;

public abstract class ApplicationException extends RuntimeException {

    protected ApplicationException(String code) {
        super(code);
    }
}
