package github.hadasbro.transport.aspects.classes;

import github.hadasbro.transport.aspects.tagInterfaces.LoggableRequest;

/**
 * GetRequest
 *
 * wrapper for http GET requests
 *
 */
public class GetRequest implements LoggableRequest {

    private String getrequest;

    public GetRequest(String getrequest) {
        this.getrequest = getrequest;
    }

    @Override
    public String toString() {
        return getrequest;
    }
}
