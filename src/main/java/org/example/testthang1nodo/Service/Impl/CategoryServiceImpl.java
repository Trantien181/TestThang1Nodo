package org.example.testthang1nodo.Service.Impl;


import org.example.testthang1nodo.DTO.DTORequest.CategoryRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategoryResponseDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategorySearchResponseDTO;
import org.example.testthang1nodo.Entity.Category;
import org.example.testthang1nodo.Entity.CategoryImage;
import org.example.testthang1nodo.Mapper.CategoryImageMapper;
import org.example.testthang1nodo.Repository.CategoryRepository;
import org.example.testthang1nodo.Mapper.CategoryMapper;
import org.example.testthang1nodo.Service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
@Service
public class CategoryServiceImpl implements CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);


    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryImageMapper categoryImageMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper, CategoryImageMapper categoryImageMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.categoryImageMapper = categoryImageMapper;
    }

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) {
        if (categoryRepository.existsByCategoryCodeAndStatus(requestDTO.getCategoryCode(), "1")) {
            throw new RuntimeException("Category code already exists");
        }
        Category category = categoryMapper.toEntity(requestDTO);
        for (CategoryImage categoryImage : category.getImages()) {
            categoryImage.setCategory(category);
        }
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponseDTO(savedCategory);

    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO, List<Long> updateImagesID) {
        Category category = categoryRepository.findByIdAndStatus(id, "1")
                .orElseThrow(() -> new RuntimeException("Category not found or deleted"));

        if (!category.getCategoryCode().equals(requestDTO.getCategoryCode()) &&
                categoryRepository.existsByCategoryCodeAndStatus(requestDTO.getCategoryCode(), "1")) {
            throw new RuntimeException("Category code already exists");
        }

        Category saveCategory = categoryMapper.toEntity(requestDTO);
        List<CategoryImage> categoryImages = category.getImages();
        List<CategoryImage> newImages = saveCategory.getImages();
        if (updateImagesID.isEmpty()) {
            for (CategoryImage categoryImage : categoryImages) {
                categoryImage.setStatus("0");
            }
        } else {
            for (CategoryImage categoryImage : categoryImages) {
                if (!updateImagesID.contains(categoryImage.getId())) {
                    categoryImage.setStatus("0");
                    categoryImage.setModifiedBy("admin");
                    categoryImage.setModifiedDate(LocalDateTime.now());
                }
            }
        }
        if (!newImages.isEmpty()) {
            for (CategoryImage newImage : newImages) {
                newImage.setCategory(category);
                newImage.setStatus("1");
                categoryImages.add(newImage);
            }
        }
        category.setName(requestDTO.getName());
        category.setCategoryCode(requestDTO.getCategoryCode());
        category.setDescription(requestDTO.getDescription());
        category.setModifiedDate(LocalDateTime.now());
        category.setModifiedBy("admin");
        Category updatedCategory = categoryRepository.save(category);
        return getCategoryById(id);
    }
    @Override
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepository.getCategoryByIdAndStatusAndImagesStatus(id);
        category.setImages(category.getImages().stream()
                .filter(image -> "1".equals(image.getStatus()))
                .collect(Collectors.toList()));

        return categoryMapper.toResponseDTO(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findByIdAndStatus(id, "1")
                .orElseThrow(() -> new RuntimeException("Category not found or deleted"));
        category.setStatus("0");
        category.setModifiedDate(LocalDateTime.now());
        category.setModifiedBy("system");
        categoryRepository.save(category);
    }

    @Override
    public CategorySearchResponseDTO searchCategories(String name, String categoryCode, LocalDateTime createdFrom, LocalDateTime createdTo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.searchCategories(name, categoryCode, createdFrom, createdTo, pageable);

        List<CategoryResponseDTO> data = categoryPage.getContent().stream()
                .map(category -> {
                    CategoryResponseDTO response = categoryMapper.toResponseDTO(category);
                    response.setImages(categoryImageMapper.toListResponseDTO(categoryRepository.findImagesByCategoryIds(response.getId())));
                    return response;
                })
                .collect(Collectors.toList());

        CategorySearchResponseDTO.PaginationDTO pagination = new CategorySearchResponseDTO.PaginationDTO(
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.hasNext(),
                categoryPage.hasPrevious()
        );

        return new CategorySearchResponseDTO(data, pagination);
    }

    @Override
    public ByteArrayOutputStream exportCategoriesToExcel(String name, String categoryCode, LocalDateTime createdFrom, LocalDateTime createdTo) {
        logger.debug("Exporting categories to Excel with params: name={}, categoryCode={}, createdFrom={}, createdTo={}",
                name, categoryCode, createdFrom, createdTo);

        // Lấy tất cả dữ liệu (không phân trang)
        Pageable pageable = Pageable.unpaged();
        Page<Category> categoryPage = categoryRepository.searchCategories(name, categoryCode, createdFrom, createdTo, pageable);
        for (Category category : categoryPage.getContent()) {
            category.setImages(categoryRepository.findImagesByCategoryIds(category.getId()));
        }
        List<Category> categories = categoryPage.getContent();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Categories");

            // Tạo header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Tên", "Mã", "Mô tả", "Ngày tạo", "Ngày sửa", "Người tạo", "Người sửa"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Tạo style cho ngày giờ
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));

            // Thêm dữ liệu
            int rowNum = 1;
            for (Category category : categories) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(category.getId());
                row.createCell(1).setCellValue(category.getName());
                row.createCell(2).setCellValue(category.getCategoryCode());
                row.createCell(3).setCellValue(category.getDescription());

                // Ngày tạo
                Cell createdDateCell = row.createCell(4);
                if (category.getCreatedDate() != null) {
                    createdDateCell.setCellValue(category.getCreatedDate());
                    createdDateCell.setCellStyle(dateStyle);
                }

                // Ngày sửa
                Cell modifiedDateCell = row.createCell(5);
                if (category.getModifiedDate() != null) {
                    modifiedDateCell.setCellValue(category.getModifiedDate());
                    modifiedDateCell.setCellStyle(dateStyle);
                }

                row.createCell(6).setCellValue(category.getCreatedBy());
                row.createCell(7).setCellValue(category.getModifiedBy() != null ? category.getModifiedBy() : "");
            }

            // Auto-size các cột
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi workbook vào ByteArrayOutputStream
            workbook.write(out);
            return out;

        } catch (IOException e) {
            logger.error("Error exporting categories to Excel", e);
            throw new RuntimeException("Failed to export categories to Excel", e);
        }
    }
    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findByStatus("1").stream()
                .map(categoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}