package org.example.testthang1nodo.DTO.DTOResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryImageResponseDTO {

    private Long id;
    private String name;
    private String status;
    private LocalDateTime createdDate;
    private String createdBy;
}