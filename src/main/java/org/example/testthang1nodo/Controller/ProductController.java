package org.example.testthang1nodo.Controller;

import jakarta.validation.Valid;
import org.example.testthang1nodo.DTO.DTORequest.ProductRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductImageResponseDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductResponseDTO;
import org.example.testthang1nodo.Service.ProductImageService;
import org.example.testthang1nodo.Service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductImageService productImageService;

    public ProductController(ProductService productService, ProductImageService productImageService) {
        this.productService = productService;
        this.productImageService = productImageService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO response = productService.createProduct(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable Long id) {
        ProductResponseDTO response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> responses = productService.getAllProducts();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO response = productService.updateProduct(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ProductImageResponseDTO> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary,
            @RequestParam(value = "createdBy", defaultValue = "system") String createdBy) {
        ProductImageResponseDTO response = productImageService.uploadImage(id, file, description, isPrimary, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<ProductImageResponseDTO>> getProductImages(@PathVariable Long id) {
        List<ProductImageResponseDTO> responses = productImageService.getImagesByProductId(id);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> getImageData(@PathVariable Long imageId) {
        byte[] imageData = productImageService.getImageData(imageId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Có thể điều chỉnh dựa trên định dạng
                .body(imageData);
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        productImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}