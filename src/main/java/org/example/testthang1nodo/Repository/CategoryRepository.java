package org.example.testthang1nodo.Repository;

import org.example.testthang1nodo.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByCategoryCodeAndStatus(String categoryCode, String status);
    Optional<Category> findByIdAndStatus(Long id, String status);
    List<Category> findByStatus(String status);
}