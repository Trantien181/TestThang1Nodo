package org.example.testthang1nodo.Service;

import org.example.testthang1nodo.DTO.DTORequest.ProductRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ApiResponse;
import org.example.testthang1nodo.DTO.DTOResponse.ProductResponseDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductSearchResponseDTO;
import org.example.testthang1nodo.Validation.ValidationGroups;
import org.springframework.validation.annotation.Validated;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductService {
    @Validated(ValidationGroups.OnCreate.class)
    ProductResponseDTO createProduct(ProductRequestDTO requestDTO);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO, List<Long> updateImagesID);
    ApiResponse deleteProduct(Long id);
    ProductSearchResponseDTO searchProducts(String name, String productCode, LocalDateTime createdFrom, LocalDateTime createdTo, List<Long> categoryIDs, int page, int size);
    ByteArrayOutputStream exportProductsToExcel(String name, String productCode, LocalDateTime createdFrom, LocalDateTime createdTo, List<Long> categoryIds);
}
