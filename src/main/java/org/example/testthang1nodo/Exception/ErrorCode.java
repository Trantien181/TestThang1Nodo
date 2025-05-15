package org.example.testthang1nodo.Exception;

public enum ErrorCode {
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "product.not.found"),
    PRODUCT_CODE_EXISTS("PRODUCT_CODE_EXISTS", "product.code.exists"),
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", "category.not.found"),
    INVALID_REQUEST("INVALID_REQUEST", "invalid.request"),
    MAPPING_ERROR("MAPPING_ERROR", "mapping.error"),
    DATABASE_ERROR("DATABASE_ERROR", "database.error"),
    PRODUCT_NAME_REQUIRED("PRODUCT_NAME_REQUIRED", "product.name.required"),
    PRODUCT_NAME_TOO_LONG("PRODUCT_NAME_TOO_LONG", "product.name.too.long"),
    PRODUCT_DESCRIPTION_TOO_LONG("PRODUCT_DESCRIPTION_TOO_LONG", "product.description.too.long"),
    PRODUCT_PRICE_REQUIRED("PRODUCT_PRICE_REQUIRED", "product.price.required"),
    PRODUCT_PRICE_INVALID("PRODUCT_PRICE_INVALID", "product.price.invalid"),
    PRODUCT_CODE_REQUIRED("PRODUCT_CODE_REQUIRED", "product.code.required"),
    PRODUCT_CODE_TOO_LONG("PRODUCT_CODE_TOO_LONG", "product.code.too.long"),
    PRODUCT_QUANTITY_REQUIRED("PRODUCT_QUANTITY_REQUIRED", "product.quantity.required"),
    PRODUCT_QUANTITY_INVALID("PRODUCT_QUANTITY_INVALID", "product.quantity.invalid"),
    PRODUCT_CATEGORIES_REQUIRED("PRODUCT_CATEGORIES_REQUIRED", "product.categories.required"),
    PRODUCT_IMAGES_REQUIRED("PRODUCT_IMAGES_REQUIRED", "product.images.required"),
    CATEGORY_CODE_EXISTS("CATEGORY_CODE_EXISTS", "category.code.exists"),
    CATEGORY_NAME_REQUIRED("CATEGORY_NAME_REQUIRED", "category.name.required"),
    CATEGORY_NAME_TOO_LONG("CATEGORY_NAME_TOO_LONG", "category.name.too.long"),
    CATEGORY_CODE_REQUIRED("CATEGORY_CODE_REQUIRED", "category.code.required"),
    CATEGORY_CODE_TOO_LONG("CATEGORY_CODE_TOO_LONG", "category.code.too.long"),
    CATEGORY_DESCRIPTION_TOO_LONG("CATEGORY_DESCRIPTION_TOO_LONG", "category.description.too.long"),
    CATEGORY_IMAGES_REQUIRED("CATEGORY_IMAGES_REQUIRED", "category.images.required"),
    CATEGORY_SREACH_CREATED_NOT_VALID("CATEGORY_SREACH_CREATED_NOT_VALID", "category.sreach.not.valid.created.date"),
    EXPORT_ERROR("EXPORT_ERROR", "export.error"),
    INVALID_FORMAT("INVALID_FORMAT", "invalid.format"),
    CATEGORY_SEARCH_FROM_AFTER_NOW("CATEGORY_SEARCH_FROM_AFTER_NOW", "category.search.from.after.now"),
    CATEGORY_SEARCH_TO_AFTER_NOW("CATEGORY_SEARCH_TO_AFTER_NOW", "category.search.from.to.now");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
