package github.hadasbro.transport.requests.responses;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import github.hadasbro.transport.webDto.DtoObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Log
@JacksonXmlRootElement(localName = "response")
@EqualsAndHashCode
@ToString
@Getter
@Setter
@SuppressWarnings("unused")
public abstract class ResultResponse<T extends DtoObject & Serializable>{

    public enum Codes {
        OK(1),
        NOT_FOUND(2),
        ERROR(3);
        Codes(int i) {}
    }

    private Codes code = Codes.OK;

    private String errors;

    private String warnings;

    protected List<T> result = new LinkedList<>();

    ResultResponse(){}

    ResultResponse(Codes code){
        this.code = code;
    }

    ResultResponse(Codes code, String error){
        this.code = code;
        this.errors = error;
    }

    /**
     * ResultResponse constructor
     * @param obj -
     */
    ResultResponse(T obj){

        try{
            this.result.add(obj);
        } catch (Exception e){
            log.info(e.toString());
        }
    }

    ResultResponse(List<T> obj){ // + fileter suuplier itp
        this.result.addAll(obj);
    }

}