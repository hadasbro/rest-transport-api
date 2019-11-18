package github.hadasbro.transport.aspects.loggerHandlers;

import github.hadasbro.transport.aspects.classes.LoggerDetails;
import github.hadasbro.transport.aspects.tagInterfaces.LoggableRequest;
import github.hadasbro.transport.aspects.tagInterfaces.LoggableResponse;
import lombok.extern.java.Log;

/**
 * PartialLogger
 *
 * This is default logger,
 * uses Lombok to log in request, response and exception to log
 * request and response are INFO by the default, exception is WARNING
 */
@Log
@SuppressWarnings("unused")
public class PartialLogger implements LoggerHandler {

    @Override
    public void init() {}

    @Override
    public void end() {}

    @Override
    public void logRequest(LoggableRequest request, LoggerDetails loggerDetails) {
        log.info("request: " + request.toString() + " | " + loggerDetails);
    }

    @Override
    public void logResponse(LoggableResponse response, LoggerDetails loggerDetails) {
        log.info("response: " + response.toString() + " | " + loggerDetails);
    }

}
