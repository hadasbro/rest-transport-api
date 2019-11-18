package github.hadasbro.transport.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString @Getter @Setter
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings({"unused", "WeakerAccess"})
public class ApiException extends Exception {

    public enum CODES {
        GENERAL("general error"),
        INCORRECT_DATA_FORMAT("Wrong input data format"),
        JOURNEY_NOACTIVE("Journey is not active"),
        JOURNEY_OPERATOR("Journey is for different cities"),
        JOURNEY_PASSENGER("Passenger and journey is incorrect"),
        CITY_NOT_OPERATOR("City dissallowed"),
        PASSENGER_NOACTIVE("Passenger not acvite"),
        PASSENGER_BLOCKED("Passenger blocked"),
        LIMIT_DAY("Daily limit exceed"),
        LIMIT_WEEK("Weekly limit exceed"),
        LIMIT_MONTH("Monthly limit exceed"),
        JOURNEY_TIMER("Journey timer"),
        OPER_NOT_FOUND("Operator not found"),
        OPER_RESTRICTED("operator restricted"),
        PASSENGER_NOT_FOUND("Passenger not found"),
        OWNER_RESTRICTED("Owner restricted"),
        JOURNEY_NOT_FOUND("Journey not found"),
        JOURNEY_RESTRICTED("Journey restricted"),
        JOURNEYL_NOT_FOUND("Journeyleg not found"),
        JOURNEYL_CLOSED("Journeyleg closed"),
        JOURNEYL_CANCELLED("Journeyleg cancelled"),
        ACTION_DUPLICATED("Duplicated action"),
        INSUFFICIENT_FUNDS("Insufficient funds"),
        AMOUNT_DOESNT_MATCH("Amount doesnt match"),
        POINT_DOESNT_EXIST("Point doesnt exist");

        String codeMsg;
        CODES(String msg){codeMsg = msg;}
    }

    private String message;

    private String techMessage;

    private CODES code = CODES.GENERAL;

    public ApiException(String message){
        this.message = message;
    }

    public ApiException(CODES code, Throwable techMessage){
        this.message = code.codeMsg;
        this.code = code;
        this.techMessage = techMessage.toString();
    }

    public ApiException(CODES code){
        this.message = code.codeMsg;
        this.code = code;
    }

    public ApiException(String message, CODES code){
        this.code = code;
        this.message = message;
    }

    public String getTechMessage() {
        return techMessage;
    }

    public void setTechMessage(String techMessage) {
        this.techMessage = techMessage;
    }

    public ApiException setTechMessage(Throwable throwableThrow) {
        this.techMessage = throwableThrow.toString();
        return this;
    }

    public void logTech(){
        // log(this.techMessage;)
    }

}
