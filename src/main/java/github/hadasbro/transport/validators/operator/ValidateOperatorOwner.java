package github.hadasbro.transport.validators.operator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OperatorOwnerValidator.class)
@SuppressWarnings({"unused"})
@Documented
public @interface ValidateOperatorOwner {
    String message() default "Operator owner is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
