package org.example.testthang1nodo.Controller;

import jakarta.validation.Valid;
import org.example.testthang1nodo.DTO.DTORequest.ProductRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ApiResponse;
import org.example.testthang1nodo.DTO.DTOResponse.ProductResponseDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductSearchResponseDTO;
import org.example.testthang1nodo.Mapper.CategoryMapper;
import org.example.testthang1nodo.Service.ProductService;
import org.example.testthang1nodo.Validation.ValidationGroups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import java.io.ByteArrayOutputStream;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    private CategoryMapper categoryMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Validated(ValidationGroups.OnCreate.class)
            @ModelAttribute ProductRequestDTO requestDTO) {
        ProductResponseDTO response = productService.createProduct(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ProductSearchResponseDTO> searchProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "productCode", required = false) String productCode,
            @RequestParam(value = "createdFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(value = "createdTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo,
            @RequestParam(value = "categoryIDs", required = false)List<Long> categoryIDs,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        ProductSearchResponseDTO response = productService.searchProducts(name, productCode, createdFrom, createdTo, categoryIDs , page, size);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/export")
    public ResponseEntity<Resource> exportProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) LocalDateTime createdFrom,
            @RequestParam(required = false) LocalDateTime createdTo,
            @RequestParam(required = false) List<Long> categoryIds) {

        ByteArrayOutputStream outputStream = productService.exportProductsToExcel(name, productCode, createdFrom, createdTo, categoryIds);
        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

        // Tạo tên file với định dạng products_YYYYMMDD_HHMMSS.xlsx
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "products_" + timestamp + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(outputStream.size())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Validated @ModelAttribute ProductRequestDTO requestDTO,
            @RequestParam(value = "updateImageID", required = false, defaultValue = "") List<Long> updateImageIDs) {
        ProductResponseDTO response = productService.updateProduct(id, requestDTO, updateImageIDs);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok( productService.deleteProduct(id));
    }
}
