package github.hadasbro.transport.validators.api;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequestTypeValidator.class)
@SuppressWarnings({"unused"})
@Documented
public @interface ValidateRequestType {
    String message() default "LoggableRequest type is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
