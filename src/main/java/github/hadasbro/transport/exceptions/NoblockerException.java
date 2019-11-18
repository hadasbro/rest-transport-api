package github.hadasbro.transport.exceptions;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString @EqualsAndHashCode(callSuper = false)
@SuppressWarnings({"unused", "WeakerAccess"})
public class NoblockerException extends Exception {

    public enum CODES {
        GENERAL,
        JOURNEY_NOACTIVE,
        JOURNEY_PASSENGER,
        CITY_NOT_OPERATOR,
        PASSENGER_NOACTIVE,
        PASSENGER_BLOCKED,
        LIMIT_DAY,
        LIMIT_WEEK,
        LIMIT_MONTH,
        JOURNEY_DATE,
        OPERATOR_NOT_FOUND,
        OPERATOR_RESTRICTED
    }

    private String message;
    private CODES code = CODES.GENERAL;

    public NoblockerException(String message){
        super(message);
        this.message = message;
    }
    public NoblockerException(CODES code){
        this("");
        this.code = code;
    }
    public NoblockerException(String message, CODES code){
        this(message);
        this.code = code;
    }

}
