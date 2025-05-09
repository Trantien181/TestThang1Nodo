package org.example.testthang1nodo.Mapper;

import org.example.testthang1nodo.DTO.DTORequest.CategoryRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategoryResponseDTO;
import org.example.testthang1nodo.Entity.Category;
import org.example.testthang1nodo.Entity.CategoryImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Entity -> Response DTO
    CategoryResponseDTO toResponseDTO(Category category);

    // Request DTO -> Entity
    @Mapping(source = "images", target = "images", qualifiedByName = "MultiPartToList")
    @Mapping(target = "status", defaultExpression = "java(\"1\")")
    @Mapping(target = "createdDate", defaultExpression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "createdBy", defaultExpression = "java(\"admin\")")
    Category toEntity(CategoryRequestDTO requestDTO);
    @Named("MultiPartToList")
    default List<CategoryImage> MultiPartToList(List<MultipartFile> images){
        if (images == null || images.isEmpty()) {return new ArrayList<>();}
        List<CategoryImage> list = new ArrayList<>();
        for (MultipartFile file : images) {
            try{
                CategoryImage categoryImage = new CategoryImage();
                categoryImage.setImageData(file.getBytes());
                categoryImage.setName(file.getOriginalFilename());
                categoryImage.setStatus("1");
                categoryImage.setCreatedBy("admin");
                categoryImage.setCreatedDate(LocalDateTime.now());
                list.add(categoryImage);
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
        return list;
    }
}