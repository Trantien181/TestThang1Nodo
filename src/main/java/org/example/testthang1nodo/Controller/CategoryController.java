package org.example.testthang1nodo.Controller;

import jakarta.validation.Valid;
import org.example.testthang1nodo.DTO.DTORequest.CategoryRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ApiResponse;
import org.example.testthang1nodo.DTO.DTOResponse.CategoryResponseDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategorySearchResponseDTO;
import org.example.testthang1nodo.Service.CategoryService;
import org.example.testthang1nodo.Validation.ValidationGroups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryResponseDTO> createCategory(@Validated(ValidationGroups.OnCreate.class) @ModelAttribute CategoryRequestDTO requestDTO) {
        CategoryResponseDTO response = categoryService.createCategory(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @Validated(ValidationGroups.OnUpdate.class) @ModelAttribute CategoryRequestDTO requestDTO,
            @RequestParam(value = "updateImageID", required = false, defaultValue = "") List<Long> updateImageIDs) {
        CategoryResponseDTO response = categoryService.updateCategory(id, requestDTO, updateImageIDs);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }

    @GetMapping("/search")
    public ResponseEntity<CategorySearchResponseDTO> searchCategories(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "categoryCode", required = false) String categoryCode,
            @RequestParam(value = "createdFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(value = "createdTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        CategorySearchResponseDTO response = categoryService.searchCategories(name, categoryCode, createdFrom, createdTo, page, size);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/export")
    public ResponseEntity<Resource> exportCategoriesToExcel(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) LocalDateTime createdFrom,
            @RequestParam(required = false) LocalDateTime createdTo) {

        // Gọi service để lấy ByteArrayOutputStream
        ByteArrayOutputStream stream = categoryService.exportCategoriesToExcel(name, categoryCode, createdFrom, createdTo);
        byte[] bytes = stream.toByteArray();

        // Tạo resource từ bytes
        ByteArrayResource resource = new ByteArrayResource(bytes);

        // Tạo tên file với timestamp để đảm bảo tên file duy nhất
        String fileName = "categories_" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

        // Thiết lập header để trình duyệt hiển thị hộp thoại lưu file
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        // Trả về response với file
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(bytes.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}