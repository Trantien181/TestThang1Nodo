package org.example.testthang1nodo.Repository;

import org.example.testthang1nodo.Entity.Product;
import org.example.testthang1nodo.Entity.ProductImage;
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
public interface ProductRepository extends JpaRepository<Product, Long> {
  boolean existsByProductCodeAndStatus(String productCode, String status);
  Optional<Product> findByIdAndStatus(Long id, String status);
  @Query("SELECT p FROM Product p " +
          "WHERE p.status = '1' " +
          "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
          "AND (:productCode IS NULL OR p.productCode = :productCode) " +
          "AND (:createdFrom IS NULL OR p.createdDate >= :createdFrom) " +
          "AND (:createdTo IS NULL OR p.createdDate <= :createdTo)")
  Page<Product> searchProducts(
          @Param("name") String name,
          @Param("productCode") String productCode,
          @Param("createdFrom") LocalDateTime createdFrom,
          @Param("createdTo") LocalDateTime createdTo,
          Pageable pageable);
  @Query("SELECT i FROM ProductImage i WHERE i.product.id IN :productIds AND (i.status = '1' OR i.status IS NULL)")
  List<ProductImage> findImagesByProductIds(@Param("productIds") List<Long> productIds);

}