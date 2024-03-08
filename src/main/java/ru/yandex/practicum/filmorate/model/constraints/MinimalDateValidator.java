package ru.yandex.practicum.filmorate.model.constraints;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Slf4j
public class MinimalDateValidator implements ConstraintValidator<MinimalDate, LocalDate> {
    private LocalDate minDate;

    @Override
    public void initialize(MinimalDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(constraintAnnotation.dateFormat());
        minDate = LocalDate.parse(constraintAnnotation.minDate(), formatter);
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        try {
            if (localDate != null && localDate.isAfter(minDate)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.warn("Date should be greater than {}", minDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
        return false;
    }
}
