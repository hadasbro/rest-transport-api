package github.hadasbro.transport.validators.api;

import github.hadasbro.transport.webDto.ApiRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class RequestTypeValidator implements ConstraintValidator<ValidateRequestType, String> {

    @Override
    public void initialize(ValidateRequestType constraintAnnotation) {}

    @Override
    public boolean isValid(String identifier, ConstraintValidatorContext context){

        Set<String> allowedTyoes = Set.of(
                ApiRequestDto.URI_BALANCE,
                ApiRequestDto.URI_INIT_JOURNEY,
                ApiRequestDto.URI_TOUCHIN,
                ApiRequestDto.URI_TOUCHOUT,
                ApiRequestDto.URI_REFUND
        );

        return allowedTyoes.contains(identifier);

//        return CollectionUtils.anyOkFromCollection(
//                Arrays.asList(ApiRequestDto.Types.values()),
//                (ev) -> ev.type.equals(identifier)
//        );

    }

}