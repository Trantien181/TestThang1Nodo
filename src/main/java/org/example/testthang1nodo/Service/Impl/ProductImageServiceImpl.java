package org.example.testthang1nodo.Service.Impl;

import org.example.testthang1nodo.DTO.DTORequest.ProductImageRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductImageResponseDTO;
import org.example.testthang1nodo.Entity.Product;
import org.example.testthang1nodo.Entity.ProductImage;
import org.example.testthang1nodo.Repository.ProductRepository;
import org.example.testthang1nodo.Repository.ProductImageRepository;
import org.example.testthang1nodo.mapper.ProductImageMapper;
import org.example.testthang1nodo.service.ProductImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductImageMapper productImageMapper;

    public ProductImageServiceImpl(ProductRepository productRepository,
                                   ProductImageRepository productImageRepository,
                                   ProductImageMapper productImageMapper) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.productImageMapper = productImageMapper;
    }

    @Override
    @Transactional
    public ProductImageResponseDTO uploadImage(Long productId, MultipartFile file, String description, boolean isPrimary) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Cập nhật trạng thái ảnh cũ nếu isPrimary = true
        if (isPrimary) {
            productImageRepository.findByProductIdAndIsPrimaryAndStatus(productId, true, "1")
                    .forEach(image -> {
                        image.setStatus("0");
                        image.setModifiedDate(LocalDate.now());
                        image.setModifiedBy("system");
                        productImageRepository.save(image);
                    });
        }

        ProductImageRequestDTO requestDTO = new ProductImageRequestDTO();
        requestDTO.setProductId(productId);
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

        ProductImage image = productImageMapper.toEntity(requestDTO);
        image.setProduct(product);
        ProductImage savedImage = productImageRepository.save(image);
        return productImageMapper.toResponseDTO(savedImage);
    }

    @Override
    public List<ProductImageResponseDTO> getImagesByProductId(Long productId) {
        return productImageRepository.findByProductIdAndStatus(productId, "1").stream()
                .map(productImageMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] getImageData(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        return image.getImageData();
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        image.setStatus("0");
        image.setModifiedDate(LocalDate.now());
        image.setModifiedBy("system");
        productImageRepository.save(image);
    }
}