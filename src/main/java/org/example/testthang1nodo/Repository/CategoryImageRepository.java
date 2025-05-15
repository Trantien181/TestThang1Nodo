package org.example.testthang1nodo.Repository;

import jakarta.transaction.Transactional;
import org.example.testthang1nodo.Entity.CategoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CategoryImageRepository extends JpaRepository<CategoryImage, Long> {
  @Modifying
  @Transactional
  @Query("UPDATE CategoryImage ci SET ci.status = :status, ci.modifiedDate = :modifiedDate, ci.modifiedBy = :modifiedBy " +
          "WHERE ci.category.id = :categoryId AND ci.status = '1'")
  void softDeleteImagesBycategoryId(
          @Param("categoryId") Long categoryId,
          @Param("status") String status,
          @Param("modifiedDate") LocalDateTime modifiedDate,
          @Param("modifiedBy") String modifiedBy
  );
}
