package org.example.testthang1nodo.Mapper;

import org.example.testthang1nodo.DTO.DTORequest.ProductRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductResponseDTO;
import org.example.testthang1nodo.Entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Entity -> Response DTO
    ProductResponseDTO toResponseDTO(Product product);

    // Request DTO -> Entity
    @Mapping(target = "id", ignore = true) // Ignore id when creating new entity
    Product toEntity(ProductRequestDTO requestDTO);
}