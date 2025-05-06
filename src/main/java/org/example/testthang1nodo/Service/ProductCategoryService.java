package org.example.testthang1nodo.Service;

import org.example.testthang1nodo.DTO.DTORequest.ProductCategoryRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductCategoryResponseDTO;
import java.util.List;

public interface ProductCategoryService {
    ProductCategoryResponseDTO createProductCategory(ProductCategoryRequestDTO requestDTO);
    List<ProductCategoryResponseDTO> getProductCategoriesByProductId(Long productId);
    void deleteProductCategory(Long productId, Long categoryId);
}