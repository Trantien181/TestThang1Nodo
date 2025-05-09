package org.example.testthang1nodo.Mapper;

import org.example.testthang1nodo.DTO.DTORequest.CategoryImageRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategoryImageResponseDTO;
import org.example.testthang1nodo.Entity.CategoryImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryImageMapper {

    // Entity -> Response DTO
    CategoryImageResponseDTO toResponseDTO(CategoryImage categoryImage);

    // Request DTO -> Entity
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "category", ignore = true) // Category sẽ được set trong service
    CategoryImage toEntity(CategoryImageRequestDTO requestDTO);
}