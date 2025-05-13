package org.example.testthang1nodo.Mapper;

import org.example.testthang1nodo.DTO.DTORequest.ProductRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductResponseDTO;
import org.example.testthang1nodo.Entity.CategoryImage;
import org.example.testthang1nodo.Entity.Product;
import org.example.testthang1nodo.Entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Entity -> Response DTO
    @Mapping(target = "images", ignore = true)
    ProductResponseDTO toResponseDTO(Product product);

    // Request DTO -> Entity
    @Mapping(source = "images", target = "images", qualifiedByName = "MultiPartToList")
    @Mapping(target = "status", defaultExpression = "java(\"1\")")
    @Mapping(target = "createdDate", defaultExpression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "createdBy", defaultExpression = "java(\"admin\")")
    Product toEntity(ProductRequestDTO requestDTO);
    @Named("MultiPartToList")
    default List<ProductImage> MultiPartToList(List<MultipartFile> images){
        if (images == null || images.isEmpty()) {return new ArrayList<>();}
        List<ProductImage> list = new ArrayList<>();
        for (MultipartFile file : images) {
            try{
                ProductImage productImage = new ProductImage();
                productImage.setImageData(file.getBytes());
                productImage.setName(file.getOriginalFilename());
                productImage.setStatus("1");
                productImage.setCreatedBy("admin");
                productImage.setCreatedDate(LocalDateTime.now());
                list.add(productImage);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}