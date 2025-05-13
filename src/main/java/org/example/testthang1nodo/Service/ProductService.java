package org.example.testthang1nodo.Service;

import org.example.testthang1nodo.DTO.DTORequest.ProductRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductResponseDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductSearchResponseDTO;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO requestDTO);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO, List<Long> updateImagesID);
    void deleteProduct(Long id);
    ProductSearchResponseDTO searchProducts(String name, String productCode, LocalDateTime createdFrom, LocalDateTime createdTo, List<Long> categoryIDs, int page, int size);
    ByteArrayOutputStream exportProductsToExcel(String name, String productCode, LocalDateTime createdFrom, LocalDateTime createdTo, List<Long> categoryIds);
}
