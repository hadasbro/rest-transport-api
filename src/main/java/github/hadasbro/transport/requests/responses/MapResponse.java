package github.hadasbro.transport.requests.responses;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;
import java.util.HashMap;
import java.util.Map;

@Log
@JacksonXmlRootElement(localName = "response")
@EqualsAndHashCode
@ToString
@Getter
@Setter
@SuppressWarnings("unused")
public class MapResponse<T,U>{

    public enum Codes {
        OK(1),
        NOT_FOUND(2),
        ERROR(3);
        Codes(int i) {}
    }

    private Codes code = Codes.OK;

    private String errors;

    private String warnings;

    protected Map<T,U> result = new HashMap<>();

    public MapResponse(){}

    public MapResponse(Codes code){
        this.code = code;
    }

    public MapResponse(Codes code, String errors){
        this.code = code;
        this.errors = errors;
    }

    public MapResponse(Map<T,U>  obj){
        this.result = obj;
    }

    public void setException(Exception ex) {
        this.errors = ex.getMessage();
    }
}