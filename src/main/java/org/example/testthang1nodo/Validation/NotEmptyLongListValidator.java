package org.example.testthang1nodo.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class NotEmptyLongListValidator implements ConstraintValidator<NotEmptyLongList, List<Long>> {

    @Override
    public boolean isValid(List<Long> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        // Đảm bảo không có giá trị null trong danh sách
        for (Long item : value) {
            if (item == null) return false;
        }
        return true;
    }
}
