package org.example.testthang1nodo.Service.Impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.example.testthang1nodo.DTO.DTORequest.ProductRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.*;
import org.example.testthang1nodo.Entity.*;
import org.example.testthang1nodo.Mapper.CategoryMapper;
import org.example.testthang1nodo.Mapper.ProductImageMapper;
import org.example.testthang1nodo.Mapper.ProductMapper;

import org.example.testthang1nodo.Repository.CategoryRepository;
import org.example.testthang1nodo.Repository.ProductCategoryRepository;
import org.example.testthang1nodo.Repository.ProductImageRepository;
import org.example.testthang1nodo.Repository.ProductRepository;
import org.example.testthang1nodo.Service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.time.format.DateTimeFormatter;


@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    ProductImageMapper productImageMapper;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ProductImageRepository productImageRepository;

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {
        if (productRepository.existsByProductCodeAndStatus(requestDTO.getProductCode(), "1")) {
            throw new RuntimeException("Product code already exists");
        }
        Product product = productMapper.toEntity(requestDTO);
        for (ProductImage productImage : product.getImages()) {
            productImage.setProduct(product);
        }
        product = productRepository.save(product);
        entityManager.flush();
        List<CategoryResponseDTO> categories = new ArrayList<>();
        List<ProductCategory> productCategories = new ArrayList<>();
        for (Long categoryID : requestDTO.getCategoryIds()) {
            Category category = categoryRepository.findByIdAndStatus(categoryID, "1")
                    .orElseThrow(() -> new RuntimeException("Category not found or deleted"));
            categories.add(new CategoryResponseDTO(categoryID, category.getName()));
            ProductCategory productCategory = new ProductCategory(product, category, LocalDateTime.now(), "admin", "1");
            productCategories.add(productCategory);
        }
        product.setProductCategories(productCategories);

        ProductResponseDTO responseDTO = productMapper.toResponseDTO(productRepository.save(product));
        responseDTO.setCategories(categories);
        return responseDTO;
    }


    @Override
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO requestDTO, List<Long> updateImagesID) {
        Product product = productRepository.findByIdAndStatus(id, "1")
                .orElseThrow(() -> new RuntimeException("Product with ID " + id + " not found or already deleted"));
        // Kiểm tra productCode trùng lặp
        if (!product.getProductCode().equals(requestDTO.getProductCode()) &&
                productRepository.existsByProductCodeAndStatus(requestDTO.getProductCode(), "1")) {
            throw new RuntimeException("Product code '" + requestDTO.getProductCode() + "' already exists");
        }
        // Xử lý ProductImage
        List<ProductImage> newImages = productMapper.toEntity(requestDTO).getImages();
        List<Long> newCategoryIds = requestDTO.getCategoryIds() != null ? requestDTO.getCategoryIds() : new ArrayList<>();

        // Xử lý ảnh cũ
        productImageRepository.updateImagesByProductIdAndNotInIds(product.getId(), updateImagesID,
                updateImagesID.isEmpty(), "0", LocalDateTime.now(), "admin"
        );

        productCategoryRepository.softDeleteCategoriesByProductIdAndNotInCategoryIds(id, newCategoryIds, "0",
                LocalDateTime.now(), "admin"
        );

        productCategoryRepository.restoreCategoriesByProductIdAndCategoryIds(id, newCategoryIds,
                "1", LocalDateTime.now(),"admin"
        );
        entityManager.clear(); // Xóa persistence context
        List<ProductImage> existingImages = productRepository.findImagesByProductIds(List.of(product.getId()));
        List<ProductCategory> existingCategories = productCategoryRepository.findByProductId(id);
        //  Xử lý ảnh mới
        if (!newImages.isEmpty()) {
            System.out.println("đã chạy voà đây");
            for (ProductImage newImage : newImages) {
                newImage.setProduct(product);
                existingImages.add(newImage);
            }
        }
        product.setImages(existingImages);
        // Cập nhật các thông tin cơ bản của Product
        product.setName(requestDTO.getName());
        product.setProductCode(requestDTO.getProductCode());
        product.setDescription(requestDTO.getDescription());
        product.setPrice(requestDTO.getPrice());
        product.setQuantity(requestDTO.getQuantity());
        product.setModifiedDate(LocalDateTime.now());
        product.setModifiedBy("admin");
        // Xử lý ProductCategory mới
        Set<Long> existingCategoryIds = existingCategories.stream()
                .map(pc -> pc.getCategory().getId())
                .collect(Collectors.toSet());
        List<Long> categoryIdsToFetch = newCategoryIds.stream()
                .filter(categoryId -> !existingCategoryIds.contains(categoryId))
                .collect(Collectors.toList());
        if (!categoryIdsToFetch.isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(categoryIdsToFetch);
            if (categories.size() != categoryIdsToFetch.size()) {
                throw new RuntimeException("One or more categories not found or inactive");
            }
            for (Category category : categories) {
                ProductCategory productCategory = new ProductCategory(
                        product, category, LocalDateTime.now(), "admin", "1");
                existingCategories.add(productCategory);
            }
        }
        product.setProductCategories(existingCategories);
        Product updatedProduct = productRepository.save(product);
        ProductResponseDTO responseDTO = productMapper.toResponseDTO(updatedProduct);
        responseDTO.setImages(existingImages.stream()
                .filter(image -> !"0".equals(image.getStatus()))
                .map(productImageMapper::toResponseDTO)
                .collect(Collectors.toList()));
        responseDTO.setCategory(existingCategories.isEmpty() ?
                "" :
                existingCategories.stream()
                        .filter(category -> "1".equals(category.getStatus()))
                        .map(pc -> pc.getCategory().getName())
                        .collect(Collectors.joining(", ")));
        return responseDTO;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndStatus(id, "1")
                .orElseThrow(() -> new RuntimeException("Category not found or deleted"));
        product.setStatus("0");
        product.setModifiedDate(LocalDateTime.now());
        product.setModifiedBy("admin");
        productImageRepository.softDeleteImagesByProductId(
                product.getId(),
                "0",
                LocalDateTime.now(),
                "admin"
        );
        productRepository.save(product);
    }

    @Override
    public ProductSearchResponseDTO searchProducts(String name, String productCode, LocalDateTime createdFrom, LocalDateTime createdTo, List<Long> categoryIds, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Tìm kiếm sản phẩm
        Page<Product> productPage = productRepository.searchProducts(name, productCode, createdFrom, createdTo, pageable);
        List<Product> products = productPage.getContent();
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        logger.info("All product IDs: {}", productIds);

        // Lấy danh sách ProductCategoryDTO
        List<ProductCategoryResponseDTO> productCategoryDTOs;
        if (categoryIds != null && !categoryIds.isEmpty()) {
            logger.info("Category IDs: {}", categoryIds);
            productCategoryDTOs = productCategoryRepository.findProductCategoriesByCategoryIds(categoryIds);
        } else {
            logger.info("No category IDs provided, fetching all categories for products");
            productCategoryDTOs = productCategoryRepository.findProductCategoriesByProductIds(productIds);
        }
        logger.info("ProductCategoryDTOs: {}", productCategoryDTOs);

        // Tạo Map<productId, categoryName>
        Map<Long, String> productCategoryMap = productCategoryDTOs.stream()
                .collect(Collectors.groupingBy(
                        ProductCategoryResponseDTO::getProductId,
                        Collectors.mapping(
                                ProductCategoryResponseDTO::getCategoryName,
                                Collectors.joining(", ")
                        )
                ));

        // Lọc sản phẩm nếu có categoryIds
        List<Product> filteredProducts;
        long totalElements;
        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Long> filteredProductIds = productCategoryDTOs.stream()
                    .map(ProductCategoryResponseDTO::getProductId)
                    .distinct()
                    .collect(Collectors.toList());
            logger.info("Filtered product IDs for categoryIds {}: {}", categoryIds, filteredProductIds);

            if (filteredProductIds.isEmpty()) {
                logger.info("No products found for categoryIds {}", categoryIds);
                return new ProductSearchResponseDTO(
                        List.of(),
                        new ProductSearchResponseDTO.PaginationDTO(0, size, 0, 0, false, false)
                );
            }

            filteredProducts = products.stream()
                    .filter(product -> filteredProductIds.contains(product.getId()))
                    .collect(Collectors.toList());
            totalElements = filteredProducts.size();
            logger.info("Filtered products: {}", filteredProducts.stream().map(Product::getId).collect(Collectors.toList()));
        } else {
            filteredProducts = products;
            totalElements = productPage.getTotalElements();
        }

        // Tạo lại Page
        Page<Product> finalProductPage = (categoryIds != null && !categoryIds.isEmpty())
                ? new PageImpl<>(filteredProducts, pageable, totalElements)
                : productPage;

        // Lấy danh sách ảnh sản phẩm
        Map<Long, List<ProductImageResponseDTO>> imageMap = productRepository.findImagesByProductIds(productIds)
                .stream()
                .filter(image -> image.getProduct() != null && image.getProduct().getId() != null)
                .collect(Collectors.groupingBy(
                        image -> image.getProduct().getId(),
                        Collectors.mapping(productImageMapper::toResponseDTO, Collectors.toList())
                ));

        // Ánh xạ sản phẩm sang DTO
        List<ProductResponseDTO> data = filteredProducts.stream()
                .map(product -> {
                    ProductResponseDTO response = productMapper.toResponseDTO(product);
                    response.setImages(imageMap.getOrDefault(product.getId(), new ArrayList<>()));
                    response.setCategory(productCategoryMap.getOrDefault(product.getId(), ""));
                    return response;
                })
                .collect(Collectors.toList());

        // Tạo thông tin phân trang
        ProductSearchResponseDTO.PaginationDTO pagination = new ProductSearchResponseDTO.PaginationDTO(
                finalProductPage.getNumber(),
                finalProductPage.getSize(),
                totalElements,
                finalProductPage.getTotalPages(),
                finalProductPage.hasNext(),
                finalProductPage.hasPrevious()
        );

        return new ProductSearchResponseDTO(data, pagination);
    }



    @Override
    public ByteArrayOutputStream exportProductsToExcel(String name, String productCode, LocalDateTime createdFrom, LocalDateTime createdTo, List<Long> categoryIds) {
        // Tìm kiếm sản phẩm (không phân trang)
        List<Product> products = productRepository.searchProducts(name, productCode, createdFrom, createdTo, null)
                .getContent();
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        logger.info("All product IDs for export: {}", productIds);

        // Lấy danh sách ProductCategoryDTO
        List<ProductCategoryResponseDTO> productCategoryDTOs;
        if (categoryIds != null && !categoryIds.isEmpty()) {
            logger.info("Category IDs for export: {}", categoryIds);
            productCategoryDTOs = productCategoryRepository.findProductCategoriesByCategoryIds(categoryIds);
        } else {
            logger.info("No category IDs provided for export, fetching all categories for products");
            productCategoryDTOs = productCategoryRepository.findProductCategoriesByProductIds(productIds);
        }
        logger.info("ProductCategoryDTOs for export: {}", productCategoryDTOs);

        // Tạo Map<productId, categoryName>
        Map<Long, String> productCategoryMap = productCategoryDTOs.stream()
                .collect(Collectors.groupingBy(
                        ProductCategoryResponseDTO::getProductId,
                        Collectors.mapping(
                                ProductCategoryResponseDTO::getCategoryName,
                                Collectors.joining(", ")
                        )
                ));

        // Lọc sản phẩm nếu có categoryIds
        List<Product> filteredProducts;
        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Long> filteredProductIds = productCategoryDTOs.stream()
                    .map(ProductCategoryResponseDTO::getProductId)
                    .distinct()
                    .collect(Collectors.toList());
            logger.info("Filtered product IDs for categoryIds {}: {}", categoryIds, filteredProductIds);

            if (filteredProductIds.isEmpty()) {
                logger.info("No products found for categoryIds {} for export", categoryIds);
                filteredProducts = new ArrayList<>();
            } else {
                filteredProducts = products.stream()
                        .filter(product -> filteredProductIds.contains(product.getId()))
                        .collect(Collectors.toList());
                logger.info("Filtered products for export: {}", filteredProducts.stream().map(Product::getId).collect(Collectors.toList()));
            }
        } else {
            filteredProducts = products;
        }

        // Tạo workbook Excel
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            // Tạo header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Tên", "Mã", "Giá", "Số lượng", "Ngày tạo", "Ngày sửa", "Danh mục"};
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Định dạng ngày
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));

            // Ghi dữ liệu sản phẩm
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < filteredProducts.size(); i++) {
                Product product = filteredProducts.get(i);
                Row row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getProductCode());
                row.createCell(3).setCellValue(product.getPrice());
                row.createCell(4).setCellValue(product.getQuantity());

                // Ngày tạo
                Cell createdDateCell = row.createCell(5);
                if (product.getCreatedDate() != null) {
                    createdDateCell.setCellValue(product.getCreatedDate().format(formatter));
                    createdDateCell.setCellStyle(dateStyle);
                }

                // Ngày sửa (giả định không có updatedDate, để trống)
                row.createCell(6).setCellValue("");

                // Danh mục
                row.createCell(7).setCellValue(productCategoryMap.getOrDefault(product.getId(), ""));
            }

            // Tự động điều chỉnh kích thước cột
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi workbook vào ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream;

        } catch (IOException e) {
            logger.error("Error exporting products to Excel", e);
            throw new RuntimeException("Failed to export products to Excel", e);
        }
    }
}
