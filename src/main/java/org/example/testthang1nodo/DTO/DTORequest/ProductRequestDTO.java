package org.example.testthang1nodo.DTO.DTORequest;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.testthang1nodo.Validation.NotEmptyLongList;
import org.example.testthang1nodo.Validation.NotEmptyMultipartList;
import org.example.testthang1nodo.Validation.ValidationGroups;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    private Long id;

    @NotBlank(message = "{product.name.required}", groups = ValidationGroups.OnCreate.class)
    @Size(max = 200, message = "{product.name.too.long}")
    private String name;

    @NotBlank(message = "{product.description.required}", groups = ValidationGroups.OnCreate.class)
    @Size(max = 200, message = "{product.description.too.long}")
    private String description;

    @NotNull(message = "{product.price.required}", groups = ValidationGroups.OnCreate.class)
    @PositiveOrZero(message = "{product.price.invalid}")
    private Double price;

    @NotBlank(message = "{product.code.required}", groups = ValidationGroups.OnCreate.class)
    @Size(max = 50, message = "{product.code.too.long}")
    private String productCode;

    @NotNull(message = "{product.quantity.required}", groups = ValidationGroups.OnCreate.class)
    @PositiveOrZero(message = "{product.quantity.invalid}")
    private Long quantity;

    private String status;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private String createdBy;

    private String modifiedBy;

    @NotEmptyLongList(message = "{product.categories.required}")
    private List<Long> categoryIds;

    @NotEmptyMultipartList(message = "{category.images.required}", groups = {ValidationGroups.OnCreate.class, ValidationGroups.OnUpdate.class})
    private List<MultipartFile> images;
}