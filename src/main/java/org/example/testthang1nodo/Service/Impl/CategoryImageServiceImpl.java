package org.example.testthang1nodo.Service.Impl;

import org.example.testthang1nodo.DTO.DTORequest.CategoryImageRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategoryImageResponseDTO;
import org.example.testthang1nodo.Entity.Category;
import org.example.testthang1nodo.Entity.CategoryImage;
import org.example.testthang1nodo.Repository.CategoryRepository;
import org.example.testthang1nodo.Repository.CategoryImageRepository;
import org.example.testthang1nodo.Mapper.CategoryImageMapper;
import org.example.testthang1nodo.Service.CategoryImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryImageServiceImpl implements CategoryImageService {

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CategoryImageRepository categoryImageRepository;
    @Autowired
    CategoryImageMapper categoryImageMapper;

    @Override
    @Transactional
    public CategoryImageResponseDTO uploadImage(Long categoryId, MultipartFile file, String description, boolean isPrimary, String createdBy) {
        Category category = categoryRepository.findByIdAndStatus(categoryId, "1")
                .orElseThrow(() -> new RuntimeException("Category not found or deleted"));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Image file is required");
        }
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Invalid image format. Only JPEG and PNG are allowed");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("Image size exceeds 10MB limit");
        }


        CategoryImageRequestDTO requestDTO = new CategoryImageRequestDTO();
        requestDTO.setCategoryId(categoryId);
//        try {
//            requestDTO.setImageData(file.getBytes());
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to read image data", e);
//        }
//        requestDTO.setDescription(description);
//        requestDTO.setIsPrimary(isPrimary);
//        requestDTO.setStatus("1");
//        requestDTO.setCreatedDate(LocalDate.now());
//        requestDTO.setCreatedBy(createdBy != null ? createdBy : "system");

        CategoryImage image = categoryImageMapper.toEntity(requestDTO);
        image.setCategory(category);
        CategoryImage savedImage = categoryImageRepository.save(image);
        return categoryImageMapper.toResponseDTO(savedImage);
    }

    @Override
    public List<CategoryImageResponseDTO> getImagesByCategoryId(Long categoryId) {
        return categoryImageRepository.findByCategoryIdAndStatus(categoryId, "1").stream()
                .map(categoryImageMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] getImageData(Long imageId) {
        CategoryImage image = categoryImageRepository.findByIdAndStatus(imageId, "1")
                .orElseThrow(() -> new RuntimeException("Image not found or deleted"));
        return image.getImageData();
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        CategoryImage image = categoryImageRepository.findByIdAndStatus(imageId, "1")
                .orElseThrow(() -> new RuntimeException("Image not found or deleted"));
        image.setStatus("0");
        image.setModifiedDate(LocalDateTime.now());
        image.setModifiedBy("system");
        categoryImageRepository.save(image);
    }
}