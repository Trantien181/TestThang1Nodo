package org.example.testthang1nodo.DTO.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageResponseDTO {

    private Long id;
    private Long productId;
    private String description;
    private Boolean isPrimary;
    private String status;
    private LocalDate createdDate;
    private LocalDate modifiedDate;
    private String createdBy;
    private String modifiedBy;
}