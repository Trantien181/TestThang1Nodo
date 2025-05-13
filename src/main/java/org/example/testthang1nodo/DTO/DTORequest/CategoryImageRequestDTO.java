package org.example.testthang1nodo.DTO.DTORequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryImageRequestDTO {

    private Long id;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;

    @NotNull(message = "Image is required")
    private MultipartFile image;

    private String status;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;
    @JsonProperty(defaultValue = "admin")
    private String createdBy;

    private String modifiedBy;
}