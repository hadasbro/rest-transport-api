package github.hadasbro.transport.requests.responses;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import github.hadasbro.transport.webDto.DtoObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@ToString
@SuppressWarnings("unused")
public class ResultResponseError<T extends DtoObject & Serializable> extends ResultResponse<T>{

    public ResultResponseError(T obj){
        super(obj);
    }

    @JacksonXmlElementWrapper(localName = "Errors")
    @JacksonXmlProperty(localName = "Error")
    public List<T> getResult() {
        return result;
    }

}
