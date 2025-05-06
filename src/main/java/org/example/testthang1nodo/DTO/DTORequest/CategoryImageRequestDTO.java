package org.example.testthang1nodo.DTO.DTORequest;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryImageRequestDTO {

    private Long id;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;

    @NotNull(message = "Image data is required")
    @Size(min = 1, message = "Image data must not be empty")
    private byte[] imageData;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private Boolean isPrimary;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "[0-1]", message = "Status must be '0' or '1'")
    private String status;

    @NotNull(message = "Created date is required")
    private LocalDate createdDate;

    private LocalDate modifiedDate;

    @NotBlank(message = "Created by is required")
    @Size(max = 100, message = "Created by must not exceed 100 characters")
    private String createdBy;

    @Size(max = 100, message = "Modified by must not exceed 100 characters")
    private String modifiedBy;
}