package com.company.weather.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CityNameValidator implements ConstraintValidator<CityNameValidation, String> {
    @Override
    public void initialize(CityNameValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        s=s.replaceAll("[^a-zA-Z0-9]","");
        return !StringUtils.isNumeric(s) && !StringUtils.isAllBlank(s);
    }
}
