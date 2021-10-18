package com.test.captcha.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class InvalidUserTypeException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public InvalidUserTypeException() {
        super(ErrorConstants.INVALID_USER_TYPE, "Invalid or Missing User Type", Status.BAD_REQUEST);
    }
}
