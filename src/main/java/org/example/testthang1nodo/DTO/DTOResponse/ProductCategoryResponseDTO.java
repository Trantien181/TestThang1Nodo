package org.example.testthang1nodo.DTO.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryResponseDTO {

//    private Long productId;
//    private Long categoryId;
//    private LocalDateTime createdDate;
//    private LocalDateTime modifiedDate;
//    private String createdBy;
//    private String modifiedBy;
    private Long productId;
    private String categoryName;
}