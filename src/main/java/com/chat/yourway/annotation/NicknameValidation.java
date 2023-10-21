package com.chat.yourway.annotation;

import com.chat.yourway.annotation.validator.NicknameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NicknameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NicknameValidation {
    String message() default "Invalid nickname";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
