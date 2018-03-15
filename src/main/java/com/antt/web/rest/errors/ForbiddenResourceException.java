package com.antt.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * Created by antt on 3/15/2018.
 */
public class ForbiddenResourceException extends AbstractThrowableProblem {
    public ForbiddenResourceException(String message) {
        super(ErrorConstants.FORBIDDEN_ACTION, "Forbidden action", Status.FORBIDDEN, message);
    }
}
