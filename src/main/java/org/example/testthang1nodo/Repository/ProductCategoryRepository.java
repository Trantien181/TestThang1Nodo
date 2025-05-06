package org.example.testthang1nodo.Repository;

import org.example.testthang1nodo.Entity.ProductCategory;
import org.example.testthang1nodo.Entity.ProductCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, ProductCategoryId> {
}