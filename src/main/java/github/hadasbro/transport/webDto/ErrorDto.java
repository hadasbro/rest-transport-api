package github.hadasbro.transport.webDto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@SuppressWarnings({"unused"})
@EqualsAndHashCode @ToString
@JacksonXmlRootElement(localName = "response")
public class ErrorDto implements DtoObject, Serializable {

    private String message;

    /**
     * ErrorDto
     * @param txt -
     */
    public ErrorDto(String txt){
        this.message = txt;
    }

    /**
     * getMessage
     * @return String
     */
    public String getMessage() {
        return message;
    }

    /**
     * setMessage
     * @param message -
     */
    public void setMessage(String message) {
        this.message = message;
    }

}