package org.example.testthang1nodo.Repository;

import org.example.testthang1nodo.Entity.CategoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryImageRepository extends JpaRepository<CategoryImage, Long> {
    List<CategoryImage> findByCategoryIdAndStatus(Long categoryId, String status);
    Optional<CategoryImage> findByIdAndStatus(Long id, String status);
}