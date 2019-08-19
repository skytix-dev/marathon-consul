package com.skytix.mconsul;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationErrorHandler {
    @Autowired
    private RegistrationRunner mRegistrationRunner;

    public void handle(Exception e) {
        mRegistrationRunner.exit(e);
    }

}
