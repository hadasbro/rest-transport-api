package github.hadasbro.transport.exceptions;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString @EqualsAndHashCode(callSuper = false)
@SuppressWarnings({"unused", "WeakerAccess"})
public class GeneralException extends Exception {

    public static final int CODE_STANDARD_ERROR = 0;
    public static final int CODE_GENERAL_ERROR = 1;
    public static final int CODE_NOT_FOUND = 2;
    public static final int CODE_NOT_TECHNICAL_ERROR = 3;

    private String message;
    private int code = CODE_STANDARD_ERROR;

    public GeneralException(String message){
        super(message);
        this.message = message;
    }

    public GeneralException(String message, int code){
        this(message);
        this.code = code;
    }

}
