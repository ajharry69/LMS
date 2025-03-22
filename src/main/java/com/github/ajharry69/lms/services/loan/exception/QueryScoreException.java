package com.github.ajharry69.lms.services.loan.exception;

import com.github.ajharry69.lms.exception.LmsException;
import org.springframework.http.HttpStatusCode;

public class QueryScoreException extends LmsException {
    public QueryScoreException(HttpStatusCode statusCode) {
        super(statusCode, "FAILED_TO_QUERY_SCORE");
    }
}
