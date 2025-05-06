package org.example.testthang1nodo.Service.Impl;

import org.example.testthang1nodo.DTO.DTORequest.CategoryImageRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategoryImageResponseDTO;
import org.example.testthang1nodo.Entity.Category;
import org.example.testthang1nodo.Entity.CategoryImage;
import org.example.testthang1nodo.Repository.CategoryRepository;
import org.example.testthang1nodo.Repository.CategoryImageRepository;
import org.example.testthang1nodo.Mapper.CategoryImageMapper;
import org.example.testthang1nodo.Service.CategoryImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryImageServiceImpl implements CategoryImageService {

    private final CategoryRepository categoryRepository;
    private final CategoryImageRepository categoryImageRepository;
    private final CategoryImageMapper categoryImageMapper;

    public CategoryImageServiceImpl(CategoryRepository categoryRepository,
                                    CategoryImageRepository categoryImageRepository,
                                    CategoryImageMapper categoryImageMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryImageRepository = categoryImageRepository;
        this.categoryImageMapper = categoryImageMapper;
    }

    @Override
    @Transactional
    public CategoryImageResponseDTO uploadImage(Long categoryId, MultipartFile file, String description, boolean isPrimary) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (isPrimary) {
            categoryImageRepository.findByCategoryIdAndIsPrimaryAndStatus(categoryId, true, "1")
                    .forEach(image -> {
                        image.setStatus("0");
                        image.setModifiedDate(LocalDate.now());
                        image.setModifiedBy("system");
                        categoryImageRepository.save(image);
                    });
        }

        CategoryImageRequestDTO requestDTO = new CategoryImageRequestDTO();
        requestDTO.setCategoryId(categoryId);
        try {
            requestDTO.setImageData(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image data", e);
        }
        requestDTO.setDescription(description);
        requestDTO.setIsPrimary(isPrimary);
        requestDTO.setStatus("1");
        requestDTO.setCreatedDate(LocalDate.now());
        requestDTO.setCreatedBy("system");

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
        CategoryImage image = categoryImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        return image.getImageData();
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        CategoryImage image = categoryImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        image.setStatus("0");
        image.setModifiedDate(LocalDate.now());
        image.setModifiedBy("system");
        categoryImageRepository.save(image);
    }
}