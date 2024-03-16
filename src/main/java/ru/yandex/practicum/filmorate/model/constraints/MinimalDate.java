package ru.yandex.practicum.filmorate.model.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = MinimalDateValidator.class)
@Documented
public @interface MinimalDate {
    String message() default "{MinimalDate.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String minDate();

    String dateFormat();
}
