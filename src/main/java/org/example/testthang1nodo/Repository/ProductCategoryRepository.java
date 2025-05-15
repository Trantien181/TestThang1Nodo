package org.example.testthang1nodo.Repository;

import jakarta.transaction.Transactional;
import org.example.testthang1nodo.DTO.DTOResponse.ProductCategoryResponseDTO;
import org.example.testthang1nodo.Entity.ProductCategory;
import org.example.testthang1nodo.Entity.ProductCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, ProductCategoryId> {
  @Query("SELECT new org.example.testthang1nodo.DTO.DTOResponse.ProductCategoryResponseDTO(pc.product.id, c.name) " +
          "FROM ProductCategory pc JOIN pc.category c WHERE pc.category.id IN :categoryIds AND pc.status = '1'")
  List<ProductCategoryResponseDTO> findProductCategoriesByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

  @Query("SELECT new org.example.testthang1nodo.DTO.DTOResponse.ProductCategoryResponseDTO(pc.product.id, c.name) " +
          "FROM ProductCategory pc JOIN pc.category c WHERE pc.product.id IN :productIds")
  List<ProductCategoryResponseDTO> findProductCategoriesByProductIds(@Param("productIds") List<Long> productIds);
  List<ProductCategory> findByProductId(Long productId);

  @Modifying
  @Transactional
  @Query("UPDATE ProductCategory pc " +
          "SET pc.status = :status, " +
          "pc.modifiedDate = :modifiedDate, " +
          "pc.modifiedBy = :modifiedBy " +
          "WHERE pc.product.id = :productId " +
          "AND pc.category.id NOT IN :categoryIds " +
          "AND pc.status = '1'")
  void softDeleteCategoriesByProductIdAndNotInCategoryIds(
          @Param("productId") Long productId,
          @Param("categoryIds") List<Long> categoryIds,
          @Param("status") String status,
          @Param("modifiedDate") LocalDateTime modifiedDate,
          @Param("modifiedBy") String modifiedBy
  );

  @Modifying
  @Transactional
  @Query("UPDATE ProductCategory pc " +
          "SET pc.status = :status, " +
          "pc.modifiedDate = :modifiedDate, " +
          "pc.modifiedBy = :modifiedBy " +
          "WHERE pc.product.id = :productId " +
          "AND pc.category.id IN :categoryIds AND pc.status = '0'")
  void restoreCategoriesByProductIdAndCategoryIds(
          @Param("productId") Long productId,
          @Param("categoryIds") List<Long> categoryIds,
          @Param("status") String status,
          @Param("modifiedDate") LocalDateTime modifiedDate,
          @Param("modifiedBy") String modifiedBy
  );
}
