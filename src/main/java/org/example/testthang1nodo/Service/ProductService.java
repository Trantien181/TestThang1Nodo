package org.example.testthang1nodo.Service;

import org.example.testthang1nodo.DTO.DTORequest.ProductRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategorySearchResponseDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductResponseDTO;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO requestDTO);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO);
    void deleteProduct(Long id);
    public ProductResponseDTO searchProducts(String name, String productCode, LocalDateTime createdFrom, LocalDateTime createdTo, int page, int size);
    ByteArrayOutputStream exportProductsToExcel(String name, String productCode, LocalDateTime createdFrom, LocalDateTime createdTo);
}
