package github.hadasbro.transport.requests.responses;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;

@Log
@JacksonXmlRootElement(localName = "response")
@EqualsAndHashCode
@ToString
@Getter
@Setter
@SuppressWarnings("unused")
public class ResultResponseSimple<T>{

    public enum Codes {
        OK(1),
        NOT_FOUND(2),
        ERROR(3);
        Codes(int i) {}
    }

    private Codes code = Codes.OK;

    private String errors;

    private String warnings;

    protected T result;

    public ResultResponseSimple(){}

    public ResultResponseSimple(Codes code){
        this.code = code;
    }

    public ResultResponseSimple(Codes code, String error){
        this.code = code;
        this.errors = error;
    }

    /**
     * ResultResponse constructor
     * @param obj -
     */
    public ResultResponseSimple(T obj){
        this.result = obj;
    }

}