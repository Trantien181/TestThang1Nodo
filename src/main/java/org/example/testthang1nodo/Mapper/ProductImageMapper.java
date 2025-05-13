package org.example.testthang1nodo.Mapper;

import org.example.testthang1nodo.DTO.DTORequest.ProductImageRequestDTO;
import org.example.testthang1nodo.DTO.DTORequest.ProductRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductImageResponseDTO;
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
public interface ProductImageMapper {

    // Entity -> Response DTO
    ProductImageResponseDTO toResponseDTO(ProductImage productImage);

    // Request DTO -> Entity
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "product", ignore = true) // Product sẽ được set trong service
    ProductImage toEntity(ProductImageRequestDTO requestDTO);
    @Mapping(target = ".", source = ".", qualifiedByName = "multiPartToProductImages")
    List<ProductImage> toEntityList(List<MultipartFile> iamges);
    @Named("multiPartToProductImages")
    default List<ProductImage> multiPartToProductImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }
        List<ProductImage> list = new ArrayList<>();
        for (MultipartFile file : images) {
            try {
                ProductImage productImage = new ProductImage();
                productImage.setImageData(file.getBytes());
                productImage.setName(file.getOriginalFilename());
                productImage.setStatus("1");
                productImage.setCreatedBy("admin");
                productImage.setCreatedDate(LocalDateTime.now());
                list.add(productImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}