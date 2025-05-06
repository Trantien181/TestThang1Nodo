package org.example.testthang1nodo.Service.Impl;

import org.example.testthang1nodo.DTO.DTORequest.ProductCategoryRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductCategoryResponseDTO;
import org.example.testthang1nodo.Entity.Product;
import org.example.testthang1nodo.Entity.Category;
import org.example.testthang1nodo.Entity.ProductCategory;
import org.example.testthang1nodo.Entity.ProductCategoryId;
import org.example.testthang1nodo.Repository.ProductRepository;
import org.example.testthang1nodo.Repository.CategoryRepository;
import org.example.testthang1nodo.Repository.ProductCategoryRepository;
import org.example.testthang1nodo.Mapper.ProductCategoryMapper;
import org.example.testthang1nodo.Service.ProductCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryMapper productCategoryMapper;

    public ProductCategoryServiceImpl(ProductCategoryRepository productCategoryRepository,
                                      ProductRepository productRepository,
                                      CategoryRepository categoryRepository,
                                      ProductCategoryMapper productCategoryMapper) {
        this.productCategoryRepository = productCategoryRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productCategoryMapper = productCategoryMapper;
    }

    @Override
    @Transactional
    public ProductCategoryResponseDTO createProductCategory(ProductCategoryRequestDTO requestDTO) {
        Product product = productRepository.findByIdAndStatus(requestDTO.getProductId(), "1")
                .orElseThrow(() -> new RuntimeException("Product not found or deleted"));
        Category category = categoryRepository.findByIdAndStatus(requestDTO.getCategoryId(), "1")
                .orElseThrow(() -> new RuntimeException("Category not found or deleted"));

        ProductCategoryId id = new ProductCategoryId(requestDTO.getProductId(), requestDTO.getCategoryId());
        if (productCategoryRepository.existsById(id)) {
            throw new RuntimeException("Product already in category");
        }

        ProductCategory productCategory = productCategoryMapper.toEntity(requestDTO);
        productCategory.setProduct(product);
        productCategory.setCategory(category);
        ProductCategory savedProductCategory = productCategoryRepository.save(productCategory);
        return productCategoryMapper.toResponseDTO(savedProductCategory);
    }

    @Override
    public List<ProductCategoryResponseDTO> getProductCategoriesByProductId(Long productId) {
        return productCategoryRepository.findByIdProductId(productId).stream()
                .filter(pc -> pc.getProduct().getStatus().equals("1") && pc.getCategory().getStatus().equals("1"))
                .map(productCategoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteProductCategory(Long productId, Long categoryId) {
        ProductCategoryId id = new ProductCategoryId(productId, categoryId);
        ProductCategory productCategory = productCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product category not found"));
        productCategoryRepository.delete(productCategory);
    }
}