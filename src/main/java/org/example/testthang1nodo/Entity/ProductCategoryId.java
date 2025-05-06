package org.example.testthang1nodo.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ProductCategoryId implements Serializable {
    private static final long serialVersionUID = 1856717841928889453L;
    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    public ProductCategoryId(@NotNull(message = "Product ID is required") @Positive(message = "Product ID must be positive") Long productId, @NotNull(message = "Category ID is required") @Positive(message = "Category ID must be positive") Long categoryId) {
    }

    public ProductCategoryId() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductCategoryId entity = (ProductCategoryId) o;
        return Objects.equals(this.productId, entity.productId) &&
                Objects.equals(this.categoryId, entity.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, categoryId);
    }

}