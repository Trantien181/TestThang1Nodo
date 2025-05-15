package org.example.testthang1nodo.DTO.DTOResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ApiResponse<T> {
    private String code;
    private String message;
    private T result;
}
