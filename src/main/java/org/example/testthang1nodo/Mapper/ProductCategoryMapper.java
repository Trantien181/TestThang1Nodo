package org.example.testthang1nodo.Mapper;

import org.example.testthang1nodo.DTO.DTORequest.ProductCategoryRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductCategoryResponseDTO;
import org.example.testthang1nodo.Entity.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {

    // Entity -> Response DTO
    @Mapping(source = "id.productId", target = "productId")
    @Mapping(source = "id.categoryId", target = "categoryId")
    ProductCategoryResponseDTO toResponseDTO(ProductCategory productCategory);

    // Request DTO -> Entity
    @Mapping(source = "productId", target = "id.productId")
    @Mapping(source = "categoryId", target = "id.categoryId")
    ProductCategory toEntity(ProductCategoryRequestDTO requestDTO);
}