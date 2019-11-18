package github.hadasbro.transport.validators.operator;

import github.hadasbro.transport.domain.transport.Owner;
import github.hadasbro.transport.services.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OperatorOwnerValidator implements ConstraintValidator<ValidateOperatorOwner, Long> {

    @Autowired
    private OperatorService operatorService;

    @Override
    public void initialize(ValidateOperatorOwner constraintAnnotation) {}

    @Override
    public boolean isValid(Long operatorId, ConstraintValidatorContext constraintValidatorContext) {
        return ValidateOperatorOwner(operatorId);
    }

    private boolean ValidateOperatorOwner(Long operatorId) {

        Owner owner = operatorService.findOwnerById(operatorId);

        return owner != null;
    }
}