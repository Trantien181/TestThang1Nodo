package org.example.testthang1nodo.DTO.DTORequest;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Category code is required")
    @Size(max = 50, message = "Category code must not exceed 50 characters")
    private String categoryCode;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    private List<MultipartFile> images;
    private String status;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private String createdBy;

}