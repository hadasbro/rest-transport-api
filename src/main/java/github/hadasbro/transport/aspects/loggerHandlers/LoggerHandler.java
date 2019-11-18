package github.hadasbro.transport.aspects.loggerHandlers;

import github.hadasbro.transport.aspects.classes.LoggerDetails;
import github.hadasbro.transport.aspects.tagInterfaces.LoggableRequest;
import github.hadasbro.transport.aspects.tagInterfaces.LoggableResponse;

@SuppressWarnings("unused")
public interface LoggerHandler {
    void init();
    void end();
    <S extends LoggableRequest> void logRequest(S request, LoggerDetails loggerDetails);
    <T extends LoggableResponse> void logResponse(T response, LoggerDetails loggerDetails);
}
