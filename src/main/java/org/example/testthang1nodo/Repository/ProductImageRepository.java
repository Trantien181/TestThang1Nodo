package org.example.testthang1nodo.Repository;

import jakarta.transaction.Transactional;
import org.example.testthang1nodo.Entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
  @Modifying
  @Transactional
  @Query("UPDATE ProductImage pi " +
          "SET pi.status = :status, " +
          "pi.modifiedDate = :modifiedDate, " +
          "pi.modifiedBy = :modifiedBy " +
          "WHERE pi.product.id = :productId " +
          "AND (pi.id NOT IN :updateImageIds " +
          "OR :updateImageIdsEmpty = true)" +
          "AND pi.status = '1'")
  void updateImagesByProductIdAndNotInIds(
          @Param("productId") Long productId,
          @Param("updateImageIds") List<Long> updateImageIds,
          @Param("updateImageIdsEmpty") boolean updateImageIdsEmpty,
          @Param("status") String status,
          @Param("modifiedDate") LocalDateTime modifiedDate,
          @Param("modifiedBy") String modifiedBy
  );
  @Modifying
  @Transactional
  @Query("UPDATE ProductImage pi SET pi.status = :status, pi.modifiedDate = :modifiedDate, pi.modifiedBy = :modifiedBy " +
          "WHERE pi.product.id = :productId AND pi.status = '1'")
  void softDeleteImagesByProductId(
          @Param("productId") Long productId,
          @Param("status") String status,
          @Param("modifiedDate") LocalDateTime modifiedDate,
          @Param("modifiedBy") String modifiedBy
  );
  }