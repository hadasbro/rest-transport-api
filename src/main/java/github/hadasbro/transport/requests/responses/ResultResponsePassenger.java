package github.hadasbro.transport.requests.responses;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import github.hadasbro.transport.webDto.DtoObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * ResultResponsePassenger
 *
 * @param <T>
 *
 * examples:
 *
 * <response>
 *     <errorCode>NOT_FOUND</errorCode><errors/><warnings/><Users/>
 * </response>
 *
 * <response>
 *     <errorCode>OK</errorCode><errors/><warnings/>
 *     <Users>
 *         <Passenger age="22">
 *             <username>testevodream7</username>
 *         </Passenger>
 *     </Users>
 * </response>
 */
@EqualsAndHashCode(callSuper = false)
@ToString @SuppressWarnings("unused")
public class ResultResponsePassenger<T extends DtoObject & Serializable> extends ResultResponse<T>{

    @JacksonXmlElementWrapper(localName = "Passengers")
    @JacksonXmlProperty(localName = "Passenger")
    public List<T> getResult() {
        return result;
    }

    public ResultResponsePassenger(Codes code) {
        super(code);
    }

    public ResultResponsePassenger(T obj){
        super(obj);
    }

    public ResultResponsePassenger(List<T> obj){
        super(obj);
    }
}