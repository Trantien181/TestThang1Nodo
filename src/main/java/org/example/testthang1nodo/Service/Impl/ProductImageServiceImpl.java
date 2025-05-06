package org.example.testthang1nodo.Service.Impl;

import org.example.testthang1nodo.DTO.DTORequest.ProductImageRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ProductImageResponseDTO;
import org.example.testthang1nodo.Entity.Product;
import org.example.testthang1nodo.Entity.ProductImage;
import org.example.testthang1nodo.Repository.ProductRepository;
import org.example.testthang1nodo.Repository.ProductImageRepository;
import org.example.testthang1nodo.Mapper.ProductImageMapper;
import org.example.testthang1nodo.Service.ProductImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

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
    public ProductImageResponseDTO uploadImage(Long productId, MultipartFile file, String description, boolean isPrimary, String createdBy) {
        Product product = productRepository.findByIdAndStatus(productId, "1")
                .orElseThrow(() -> new RuntimeException("Product not found or deleted"));

        // Kiểm tra file ảnh
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Image file is required");
        }
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Invalid image format. Only JPEG and PNG are allowed");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("Image size exceeds 10MB limit");
        }

        // Cập nhật trạng thái ảnh cũ nếu isPrimary = true
        if (isPrimary) {
            productImageRepository.findByProductIdAndIsPrimaryAndStatus(productId, true, "1")
                    .forEach(image -> {
                        image.setStatus("0");
                        image.setModifiedDate(LocalDate.now());
                        image.setModifiedBy(createdBy != null ? createdBy : "system");
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
        requestDTO.setCreatedBy(createdBy != null ? createdBy : "system");

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
        ProductImage image = productImageRepository.findByIdAndStatus(imageId, "1")
                .orElseThrow(() -> new RuntimeException("Image not found or deleted"));
        return image.getImageData();
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        ProductImage image = productImageRepository.findByIdAndStatus(imageId, "1")
                .orElseThrow(() -> new RuntimeException("Image not found or deleted"));
        image.setStatus("0");
        image.setModifiedDate(LocalDate.now());
        image.setModifiedBy("system");
        productImageRepository.save(image);
    }
}