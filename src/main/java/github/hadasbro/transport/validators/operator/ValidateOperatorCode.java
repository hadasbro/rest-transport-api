package github.hadasbro.transport.validators.operator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OperatorCodeValidator.class)
@SuppressWarnings({"unused"})
@Documented
public @interface ValidateOperatorCode {
    String message() default "Operator identifier is not unique or invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
