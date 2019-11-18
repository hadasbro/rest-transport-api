package github.hadasbro.transport.exceptions;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString @EqualsAndHashCode(callSuper = false)
public class ValidationException extends ApiException {

    public ValidationException(String message){
        super(message, CODES.GENERAL);
    }

}
