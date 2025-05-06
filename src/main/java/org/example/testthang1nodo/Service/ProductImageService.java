package org.example.testthang1nodo.Service;

import org.example.testthang1nodo.DTO.DTOResponse.ProductImageResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProductImageService {
    public ProductImageResponseDTO uploadImage(Long productId, MultipartFile file, String description, boolean isPrimary, String createdBy);
    List<ProductImageResponseDTO> getImagesByProductId(Long productId);
    byte[] getImageData(Long imageId);
    void deleteImage(Long imageId);
}