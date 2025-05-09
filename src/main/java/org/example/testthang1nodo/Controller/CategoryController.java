package org.example.testthang1nodo.Controller;

import jakarta.validation.Valid;
import org.example.testthang1nodo.DTO.DTORequest.CategoryRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategoryImageResponseDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategoryResponseDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategorySearchResponseDTO;
import org.example.testthang1nodo.Service.CategoryImageService;
import org.example.testthang1nodo.Service.CategoryService;
import org.example.testthang1nodo.Service.Impl.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    CategoryService categoryService;
    @Autowired
    CategoryImageService categoryImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @ModelAttribute CategoryRequestDTO requestDTO) {
        CategoryResponseDTO response = categoryService.createCategory(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute CategoryRequestDTO requestDTO,
            @RequestParam(value = "updateImageID") List<Long> updateImageIDs) {
        CategoryResponseDTO response = categoryService.updateCategory(id, requestDTO, updateImageIDs);
        return ResponseEntity.ok(response);
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
    @GetMapping("/search")
    public ResponseEntity<CategorySearchResponseDTO> searchCategories(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "categoryCode", required = false) String categoryCode,
            @RequestParam(value = "createdFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(value = "createdTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        System.out.println(name+": "+categoryCode+": "+createdFrom+": "+createdTo);
        CategorySearchResponseDTO response = categoryService.searchCategories(name, categoryCode, createdFrom, createdTo, page, size);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCategories(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "categoryCode", required = false) String categoryCode,
            @RequestParam(value = "createdFrom", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdFrom,
            @RequestParam(value = "createdTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdTo) {
        logger.info("Exporting categories with params: name={}, categoryCode={}, createdFrom={}, createdTo={}",
                name, categoryCode, createdFrom, createdTo);

        ByteArrayOutputStream out = categoryService.exportCategoriesToExcel(name, categoryCode, createdFrom, createdTo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=categories.xlsx");
        headers.setContentLength(out.size());

        return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
    }
}