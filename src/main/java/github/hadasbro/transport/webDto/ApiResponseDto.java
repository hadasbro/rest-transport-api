package github.hadasbro.transport.webDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import github.hadasbro.transport.aspects.tagInterfaces.LoggableResponse;
import github.hadasbro.transport.domain.passenger.Passenger;
import github.hadasbro.transport.exceptions.ApiException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true, value = "exc")
@ToString(exclude = "exc")
@EqualsAndHashCode(exclude = "exc")
@Getter @Setter
@Embeddable
@SuppressWarnings({"unused"})
public class ApiResponseDto implements DtoObject, Serializable, LoggableResponse {

    private static final long serialVersionUID = 4865903039190150223L;

    final public static String STATUS_OK = "OK";
    final public static String STATUS_ERROR = "ERROR";

    private ApiException exc = null;
    private Long passengerId;
    private BigDecimal balance;
    private String message = "";
    private String status = STATUS_OK;
    private String errorCode = "";

    public ApiResponseDto(){}

    public ApiResponseDto(ApiException.CODES code) {
        this(new ApiException(code));
    }

    public ApiResponseDto(ApiException error){
        this.exc = error;
        this.passengerId = 0L;
        this.balance = new BigDecimal(0);
        this.message = error.getMessage();
        this.errorCode = error.getCode().name();
        this.status = STATUS_ERROR;
    }

    public ApiResponseDto(Passenger passenger) {
        this.passengerId = passenger.getId();
        this.balance = passenger.getBalance();
    }

}