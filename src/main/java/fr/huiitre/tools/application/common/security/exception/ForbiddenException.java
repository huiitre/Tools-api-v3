package fr.huiitre.tools.application.common.security.exception;

import fr.huiitre.tools.application.common.error.ApplicationException;
import fr.huiitre.tools.application.core.module.ModuleCode;

public class ForbiddenException extends ApplicationException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(ModuleCode moduleCode) {
        super("Access forbidden for module: " + moduleCode.name());
    }
}
