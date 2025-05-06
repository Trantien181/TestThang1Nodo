package org.example.testthang1nodo.Controller;

import jakarta.validation.Valid;
import org.example.testthang1nodo.DTO.DTORequest.ProductCategoryRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductCategoryResponseDTO;
import org.example.testthang1nodo.Service.ProductCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-categories")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @PostMapping
    public ResponseEntity<ProductCategoryResponseDTO> createProductCategory(@Valid @RequestBody ProductCategoryRequestDTO requestDTO) {
        ProductCategoryResponseDTO response = productCategoryService.createProductCategory(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductCategoryResponseDTO>> getProductCategories(@PathVariable Long productId) {
        List<ProductCategoryResponseDTO> responses = productCategoryService.getProductCategoriesByProductId(productId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{productId}/{categoryId}")
    public ResponseEntity<Void> deleteProductCategory(@PathVariable Long productId, @PathVariable Long categoryId) {
        productCategoryService.deleteProductCategory(productId, categoryId);
        return ResponseEntity.noContent().build();
    }
}