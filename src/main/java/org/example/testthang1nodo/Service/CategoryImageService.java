package org.example.testthang1nodo.Service;

import org.example.testthang1nodo.DTO.DTOResponse.CategoryImageResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface CategoryImageService {
    public CategoryImageResponseDTO uploadImage(Long categoryId, MultipartFile file, String description, boolean isPrimary, String createdBy);
    List<CategoryImageResponseDTO> getImagesByCategoryId(Long categoryId);
    byte[] getImageData(Long imageId);
    void deleteImage(Long imageId);
}