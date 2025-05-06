package org.example.testthang1nodo.Repository;

import org.example.testthang1nodo.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByProductCodeAndStatus(String productCode, String status);
    Optional<Product> findByIdAndStatus(Long id, String status);
    List<Product> findByStatus(String status);
}