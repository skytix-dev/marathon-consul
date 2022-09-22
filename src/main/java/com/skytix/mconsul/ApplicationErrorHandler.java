package com.skytix.mconsul;

import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Component
public class ApplicationErrorHandler implements ErrorHandler {
    private final RegistrationRunner mRegistrationRunner;

    public ApplicationErrorHandler(RegistrationRunner mRegistrationRunner) {
        this.mRegistrationRunner = mRegistrationRunner;
    }

    @Override
    public void handleError(Throwable t) {
        mRegistrationRunner.exit(t);
    }

}
