package org.example.testthang1nodo.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class MultipartListValidator implements ConstraintValidator<NotEmptyMultipartList, List<MultipartFile>> {

    @Override
    public boolean isValid(List<MultipartFile> value, ConstraintValidatorContext context) {
        return value != null && !value.isEmpty() && value.stream().allMatch(file -> !file.isEmpty());
    }
}
