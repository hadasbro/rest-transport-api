package github.hadasbro.transport.validators.operator;

import github.hadasbro.transport.services.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OperatorCodeValidator implements ConstraintValidator<ValidateOperatorCode, String> {

    @Autowired
    private OperatorService operatorService;

    private static final String IDENTIFIER_PATTERN = "^[_A-Za-z0-9-+]+$";

    @Override
    public void initialize(ValidateOperatorCode constraintAnnotation) {}

    @Override
    public boolean isValid(String identifier, ConstraintValidatorContext context){
        return (validateOperatorCode(identifier));
    }

    private boolean validateOperatorCode(String identifier) {

        if(identifier == null) {
            return false;
        }

        try {

            Pattern pattern = Pattern.compile(IDENTIFIER_PATTERN);
            Matcher matcher = pattern.matcher(identifier);
            if(!matcher.matches()) {
                return false;
            }

            //check if identifier is unique

            return operatorService.checkIfCodeIsAvailable(identifier);

        } catch(NullPointerException e){
            return false;
        }

    }
}