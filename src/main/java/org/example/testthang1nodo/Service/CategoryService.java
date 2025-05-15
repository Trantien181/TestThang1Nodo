package org.example.testthang1nodo.Service;

import org.example.testthang1nodo.DTO.DTORequest.CategoryRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ApiResponse;
import org.example.testthang1nodo.DTO.DTOResponse.CategoryResponseDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategorySearchResponseDTO;
import org.example.testthang1nodo.Validation.ValidationGroups;
import org.springframework.validation.annotation.Validated;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

public interface CategoryService {
    @Validated(ValidationGroups.OnCreate.class)
    CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO);
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO, List<Long> updateImagesID);
    ApiResponse deleteCategory(Long id);
    public CategorySearchResponseDTO searchCategories(String name, String categoryCode, LocalDateTime createdFrom, LocalDateTime createdTo, int page, int size);
    ByteArrayOutputStream exportCategoriesToExcel(String name, String categoryCode, LocalDateTime createdFrom, LocalDateTime createdTo);
}