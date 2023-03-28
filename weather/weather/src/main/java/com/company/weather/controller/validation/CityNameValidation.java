package com.company.weather.controller.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(
        validatedBy = {CityNameValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CityNameValidation {

    String message() default "Invalid City Name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
