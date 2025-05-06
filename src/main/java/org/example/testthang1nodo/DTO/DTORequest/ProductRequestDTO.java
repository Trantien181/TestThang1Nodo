package org.example.testthang1nodo.DTO.DTORequest;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be non-negative")
    private Double price;

    @NotBlank(message = "Product code is required")
    @Size(max = 50, message = "Product code must not exceed 50 characters")
    private String productCode;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be non-negative")
    private Long quantity;

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