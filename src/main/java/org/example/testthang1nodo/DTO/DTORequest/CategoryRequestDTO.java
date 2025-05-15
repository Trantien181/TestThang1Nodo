package org.example.testthang1nodo.DTO.DTORequest;

import jakarta.validation.constraints.*;
import lombok.*;
import org.example.testthang1nodo.Validation.NotEmptyMultipartList;
import org.example.testthang1nodo.Validation.ValidationGroups;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDTO {

    private Long id;

    @NotBlank(message = "{category.name.required}", groups = ValidationGroups.OnCreate.class)
    @Size(max = 100, message = "{category.name.too.long}")
    private String name;

    @NotBlank(message = "{category.code.required}", groups = ValidationGroups.OnCreate.class)
    @Size(max = 50, message = "{category.code.too.long}")
    private String categoryCode;

    @NotBlank(message = "{category.description.required}", groups = ValidationGroups.OnCreate.class)
    @Size(max = 200, message = "{category.description.too.long}")
    private String description;

    @NotEmptyMultipartList(message = "{category.images.required}", groups = ValidationGroups.OnCreate.class)
    private List<MultipartFile> images;

    private String status;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private String createdBy;

}