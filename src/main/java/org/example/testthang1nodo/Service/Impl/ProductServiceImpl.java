package org.example.testthang1nodo.Service.Impl;

import jakarta.transaction.Transactional;
import org.example.testthang1nodo.DTO.DTORequest.ProductRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductResponseDTO;
import org.example.testthang1nodo.Entity.Category;
import org.example.testthang1nodo.Entity.Product;
import org.example.testthang1nodo.Entity.ProductCategory;
import org.example.testthang1nodo.Entity.ProductImage;
import org.example.testthang1nodo.Mapper.ProductImageMapper;
import org.example.testthang1nodo.Mapper.ProductMapper;

import org.example.testthang1nodo.Repository.CategoryRepository;
import org.example.testthang1nodo.Repository.ProductRepository;
import org.example.testthang1nodo.Service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    ProductImageMapper productImageMapper;
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        if (productRepository.existsByProductCodeAndStatus(requestDTO.getProductCode(), "1")) {
            throw new RuntimeException("Product code already exists");
        }
        Product product = productMapper.toEntity(requestDTO);
        for (ProductImage productImage : product.getImages()) {
            productImage.setProduct(product);
        }
        product = productRepository.save(product);
        List<ProductCategory> productCategories = new ArrayList<>();
        for (Long categoryID : requestDTO.getCategoryIds()){
            Category category = categoryRepository.findByIdAndStatus(categoryID, "1")
                    .orElseThrow(() -> new RuntimeException("Category not found or deleted"));;
            ProductCategory productCategory = new ProductCategory(product,category, LocalDateTime.now(),"admin");
            productCategories.add(productCategory);
        }
        // đồng bộ
        for (ProductCategory productCategory : productCategories) {
            productCategory.getCategory().getProductCategories().add(productCategory);
        }
        product.setProductCategories(productCategories);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
//        return productMapper.toResponseDTO(product);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO) {
        return null;
    }

    @Override
    public void deleteProduct(Long id) {

    }

    @Override
    public ProductResponseDTO searchProducts(String name, String productCode, LocalDateTime createdFrom, LocalDateTime createdTo, int page, int size) {
        return null;
    }

    @Override
    public ByteArrayOutputStream exportProductsToExcel(String name, String productCode, LocalDateTime createdFrom, LocalDateTime createdTo) {
        return null;
    }
}
