package org.example.testthang1nodo.Controller;

import jakarta.validation.Valid;
import org.example.testthang1nodo.DTO.DTORequest.CategoryRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategoryImageResponseDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategoryResponseDTO;
import org.example.testthang1nodo.Service.CategoryImageService;
import org.example.testthang1nodo.Service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryImageService categoryImageService;

    public CategoryController(CategoryService categoryService, CategoryImageService categoryImageService) {
        this.categoryService = categoryService;
        this.categoryImageService = categoryImageService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO requestDTO) {
        CategoryResponseDTO response = categoryService.createCategory(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategory(@PathVariable Long id) {
        CategoryResponseDTO response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> responses = categoryService.getAllCategories();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO requestDTO) {
        CategoryResponseDTO response = categoryService.updateCategory(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<CategoryImageResponseDTO> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary,
            @RequestParam(value = "createdBy", defaultValue = "system") String createdBy) {
        CategoryImageResponseDTO response = categoryImageService.uploadImage(id, file, description, isPrimary, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<CategoryImageResponseDTO>> getCategoryImages(@PathVariable Long id) {
        List<CategoryImageResponseDTO> responses = categoryImageService.getImagesByCategoryId(id);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> getImageData(@PathVariable Long imageId) {
        byte[] imageData = categoryImageService.getImageData(imageId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Có thể điều chỉnh dựa trên định dạng
                .body(imageData);
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        categoryImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}