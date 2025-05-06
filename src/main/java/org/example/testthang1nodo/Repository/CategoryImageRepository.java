package org.example.testthang1nodo.Repository;

import org.example.testthang1nodo.Entity.CategoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryImageRepository extends JpaRepository<CategoryImage, Long> {
}