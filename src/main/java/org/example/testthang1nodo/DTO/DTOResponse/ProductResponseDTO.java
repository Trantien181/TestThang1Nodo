package org.example.testthang1nodo.DTO.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String productCode;
    private String description;
    private Double price;
    private Long quantity;
    private String status;
    private List<CategoryResponseDTO> categories;
    private List<ProductImageResponseDTO> images;
    private LocalDate createdDate;
    private String createdBy;

}