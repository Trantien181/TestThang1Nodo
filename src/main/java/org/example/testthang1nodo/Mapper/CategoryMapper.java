package org.example.testthang1nodo.Mapper;

import org.example.testthang1nodo.DTO.DTORequest.CategoryRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategoryResponseDTO;
import org.example.testthang1nodo.Entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Entity -> Response DTO
    CategoryResponseDTO toResponseDTO(Category category);

    // Request DTO -> Entity
    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryRequestDTO requestDTO);
}