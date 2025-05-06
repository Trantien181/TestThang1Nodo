package org.example.testthang1nodo.Repository;

import org.example.testthang1nodo.Entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductIdAndStatus(Long productId, String status);
    List<ProductImage> findByProductIdAndIsPrimaryAndStatus(Long productId, boolean isPrimary, String status);
    Optional<ProductImage> findByIdAndStatus(Long id, String status);
}