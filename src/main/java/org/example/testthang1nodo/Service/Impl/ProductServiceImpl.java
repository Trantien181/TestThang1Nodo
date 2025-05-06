package org.example.testthang1nodo.Service.Impl;

import org.example.testthang1nodo.DTO.DTORequest.ProductRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductResponseDTO;
import org.example.testthang1nodo.Entity.Product;
import org.example.testthang1nodo.Repository.ProductRepository;
import org.example.testthang1nodo.mapper.ProductMapper;
import org.example.testthang1nodo.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        // Kiểm tra product_code duy nhất
        if (productRepository.existsByProductCode(requestDTO.getProductCode())) {
            throw new RuntimeException("Product code already exists");
        }

        Product product = productMapper.toEntity(requestDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productMapper.toResponseDTO(product);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra product_code duy nhất (nếu thay đổi)
        if (!product.getProductCode().equals(requestDTO.getProductCode()) &&
                productRepository.existsByProductCode(requestDTO.getProductCode())) {
            throw new RuntimeException("Product code already exists");
        }

        product.setName(requestDTO.getName());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setProductCode(requestDTO.getProductCode());
        product.setQuantity(requestDTO.getQuantity());
        product.setStatus(requestDTO.getStatus());
        product.setModifiedDate(LocalDate.now());
        product.setModifiedBy(requestDTO.getModifiedBy());

        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStatus("0");
        product.setModifiedDate(LocalDate.now());
        productRepository.save(product);
    }
}