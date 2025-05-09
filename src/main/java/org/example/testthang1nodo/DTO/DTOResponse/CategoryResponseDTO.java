package org.example.testthang1nodo.DTO.DTOResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.testthang1nodo.Entity.CategoryImage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponseDTO {

    private Long id;
    private String name;
    private String categoryCode;
    private String description;
    private String status;
    private List<CategoryImageResponseDTO> images;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String createdBy;

}