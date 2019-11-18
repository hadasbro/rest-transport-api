package github.hadasbro.transport.requests.responses;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import github.hadasbro.transport.webDto.DtoObject;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@ToString @NoArgsConstructor
public class ResultResponseOperator<T extends DtoObject & Serializable> extends ResultResponse<T>{

    @JacksonXmlElementWrapper(localName = "Operators")
    @JacksonXmlProperty(localName = "Operator")
    public List<T> getResult() {
        return result;
    }

    public ResultResponseOperator(Codes code) {
        super(code);
    }

    public ResultResponseOperator(Codes code, String errorMessage) {
        super(code, errorMessage);
    }

    public ResultResponseOperator(T obj){
        super(obj);
    }

    public ResultResponseOperator(List<T> obj){
        super(obj);
    }
}