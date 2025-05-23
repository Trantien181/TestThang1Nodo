package org.example.testthang1nodo.Repository;

import org.example.testthang1nodo.Entity.Category;
import org.example.testthang1nodo.Entity.CategoryImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByCategoryCodeAndStatus(String categoryCode, String status);
    Optional<Category> findByIdAndStatus(Long id, String status);
    @Query("SELECT c FROM Category c " +
            "WHERE c.status = '1' " +
            "AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:categoryCode IS NULL OR c.categoryCode = :categoryCode) " +
            "AND (:createdFrom IS NULL OR c.createdDate >= :createdFrom) " +
            "AND (:createdTo IS NULL OR c.createdDate <= :createdTo)")
    Page<Category> searchCategories(
            @Param("name") String name,
            @Param("categoryCode") String categoryCode,
            @Param("createdFrom") LocalDateTime createdFrom,
            @Param("createdTo") LocalDateTime createdTo,
            Pageable pageable);
    @Query("SELECT i FROM CategoryImage i WHERE i.category.id IN :categoryIds AND (i.status = '1' OR i.status IS NULL)")
    List<CategoryImage> findImagesByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

}