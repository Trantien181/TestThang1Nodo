package org.example.testthang1nodo.DTO.DTOResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String productCode;
    private String description;
    private Double price;
    private Long quantity;
    private String status;
    private List<CategoryResponseDTO> categories;
    private String category;
    private List<ProductImageResponseDTO> images;
    private LocalDateTime createdDate;
    private String createdBy;

}