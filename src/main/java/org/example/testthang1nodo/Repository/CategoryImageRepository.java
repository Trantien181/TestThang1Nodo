package org.example.testthang1nodo.Repository;

import org.example.testthang1nodo.Entity.CategoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryImageRepository extends JpaRepository<CategoryImage, Long> {
    List<CategoryImage> findByCategoryIdAndStatus(Long categoryId, String status);
    List<CategoryImage> findByCategoryIdAndIsPrimaryAndStatus(Long categoryId, boolean isPrimary, String status);
}