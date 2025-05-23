package org.example.testthang1nodo.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MultipartListValidator.class)
public @interface NotEmptyMultipartList {
    String message() default "Danh sách ảnh không được để trống";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}