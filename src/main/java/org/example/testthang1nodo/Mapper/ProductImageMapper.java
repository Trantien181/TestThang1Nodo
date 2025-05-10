package org.example.testthang1nodo.Mapper;

import org.example.testthang1nodo.DTO.DTORequest.ProductImageRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductImageResponseDTO;
import org.example.testthang1nodo.Entity.ProductImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    // Entity -> Response DTO
    ProductImageResponseDTO toResponseDTO(ProductImage productImage);

    // Request DTO -> Entity
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "product", ignore = true) // Product sẽ được set trong service
    ProductImage toEntity(ProductImageRequestDTO requestDTO);
}