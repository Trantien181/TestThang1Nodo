package org.example.testthang1nodo.Service.Impl;

import org.example.testthang1nodo.DTO.DTORequest.CategoryRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategoryResponseDTO;
import org.example.testthang1nodo.Entity.Category;
import org.example.testthang1nodo.Repository.CategoryRepository;
import org.example.testthang1nodo.Mapper.CategoryMapper;
import org.example.testthang1nodo.Service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) {
        if (categoryRepository.existsByCategoryCodeAndStatus(requestDTO.getCategoryCode(), "1")) {
            throw new RuntimeException("Category code already exists");
        }

        Category category = categoryMapper.toEntity(requestDTO);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseDTO(savedCategory);
    }

    @Override
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepository.findByIdAndStatus(id, "1")
                .orElseThrow(() -> new RuntimeException("Category not found or deleted"));
        return categoryMapper.toResponseDTO(category);
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findByStatus("1").stream()
                .map(categoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO) {
        Category category = categoryRepository.findByIdAndStatus(id, "1")
                .orElseThrow(() -> new RuntimeException("Category not found or deleted"));

        if (!category.getCategoryCode().equals(requestDTO.getCategoryCode()) &&
                categoryRepository.existsByCategoryCodeAndStatus(requestDTO.getCategoryCode(), "1")) {
            throw new RuntimeException("Category code already exists");
        }

        category.setName(requestDTO.getName());
        category.setCategoryCode(requestDTO.getCategoryCode());
        category.setDescription(requestDTO.getDescription());
        category.setStatus(requestDTO.getStatus());
        category.setModifiedDate(LocalDate.now());
        category.setModifiedBy(requestDTO.getModifiedBy() != null ? requestDTO.getModifiedBy() : "system");

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findByIdAndStatus(id, "1")
                .orElseThrow(() -> new RuntimeException("Category not found or deleted"));
        category.setStatus("0");
        category.setModifiedDate(LocalDate.now());
        category.setModifiedBy("system");
        categoryRepository.save(category);
    }
}