package org.example.testthang1nodo.Repository;

import org.example.testthang1nodo.Entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductIdAndStatus(Long productId, String status);
    List<ProductImage> findByProductIdAndIsPrimaryAndStatus(Long productId, boolean isPrimary, String status);
}