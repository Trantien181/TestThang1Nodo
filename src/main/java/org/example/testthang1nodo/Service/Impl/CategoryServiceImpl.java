package org.example.testthang1nodo.Service.Impl;

import org.example.testthang1nodo.DTO.DTORequest.CategoryRequestDTO;
import org.example.testthang1nodo.DTO.DTOResponse.ApiResponse;
import org.example.testthang1nodo.DTO.DTOResponse.CategoryImageResponseDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategoryResponseDTO;
import org.example.testthang1nodo.DTO.DTOResponse.CategorySearchResponseDTO;
import org.example.testthang1nodo.Entity.Category;
import org.example.testthang1nodo.Entity.CategoryImage;
import org.example.testthang1nodo.Exception.AppException;
import org.example.testthang1nodo.Exception.ErrorCode;
import org.example.testthang1nodo.Mapper.CategoryImageMapper;
import org.example.testthang1nodo.Repository.CategoryImageRepository;
import org.example.testthang1nodo.Repository.CategoryRepository;
import org.example.testthang1nodo.Mapper.CategoryMapper;
import org.example.testthang1nodo.Service.CategoryService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class CategoryServiceImpl implements CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);


    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryImageMapper categoryImageMapper;
    private final CategoryImageRepository categoryImageRepository;
    private final MessageSource messageSource;


    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper, CategoryImageMapper categoryImageMapper, CategoryImageRepository categoryImageRepository, MessageSource messageSource) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.categoryImageMapper = categoryImageMapper;
        this.categoryImageRepository = categoryImageRepository;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional(rollbackFor = IOException.class)
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) {
        if (categoryRepository.existsByCategoryCodeAndStatus(requestDTO.getCategoryCode(), "1")) {
            throw new AppException(ErrorCode.CATEGORY_CODE_EXISTS);
        }
        Category category = categoryMapper.toEntity(requestDTO);
        for (CategoryImage categoryImage : category.getImages()) {
            categoryImage.setCategory(category);
        }
        Category savedCategory = categoryRepository.save(category);
        CategoryResponseDTO categoryResponseDTO = categoryMapper.toResponseDTO(savedCategory);
        categoryResponseDTO.setImages(savedCategory.getImages().stream()
                .map(categoryImageMapper::toResponseDTO)
                .collect(Collectors.toList()));
        return categoryResponseDTO;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO, List<Long> updateImagesID) {
        Category category = categoryRepository.findByIdAndStatus(id, "1")
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (!category.getCategoryCode().equals(requestDTO.getCategoryCode()) &&
                categoryRepository.existsByCategoryCodeAndStatus(requestDTO.getCategoryCode(), "1")) {
            throw new AppException(ErrorCode.CATEGORY_CODE_EXISTS);
        }

        Category saveCategory = categoryMapper.toEntity(requestDTO);

        List<CategoryImage> categoryImages = category.getImages();

        List<CategoryImage> newImages = saveCategory.getImages();

        for (CategoryImage categoryImage : categoryImages) {
            if (updateImagesID.isEmpty() || !updateImagesID.contains(categoryImage.getId())) {
                categoryImage.setStatus("0");
                categoryImage.setModifiedBy("admin");
                categoryImage.setModifiedDate(LocalDateTime.now());
            }
        }
        if (!newImages.isEmpty()) {
            for (CategoryImage newImage : newImages) {
                newImage.setCategory(category);
                newImage.setStatus("1");
                categoryImages.add(newImage);
            }
        }
        if (requestDTO.getName() != null && !requestDTO.getName().isBlank()) {
            category.setName(requestDTO.getName());
        }
        if (requestDTO.getCategoryCode() != null && !requestDTO.getCategoryCode().isBlank()) {
            category.setCategoryCode(requestDTO.getCategoryCode());
        }
        if (requestDTO.getDescription() != null && !requestDTO.getDescription().isBlank()) {
            category.setDescription(requestDTO.getDescription());
        }
        category.setModifiedDate(LocalDateTime.now());
        category.setModifiedBy("admin");
        Category updatedCategory = categoryRepository.save(category);
        CategoryResponseDTO responseDTO = categoryMapper.toResponseDTO(updatedCategory);
        responseDTO.setImages( category.getImages().stream()
                        .filter(image -> !"0".equals(image.getStatus()))
                        .map(categoryImageMapper::toResponseDTO)
                        .collect(Collectors.toList())
        );
        return responseDTO;
    }

    @Override
    @Transactional
    public ApiResponse deleteCategory(Long id) {
        Category category = categoryRepository.findByIdAndStatus(id, "1")
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        category.setStatus("0");
        category.setModifiedDate(LocalDateTime.now());
        category.setModifiedBy("admin");
        categoryImageRepository.softDeleteImagesBycategoryId(
                category.getId(),
                "0",
                LocalDateTime.now(),
                "admin");
        categoryRepository.save(category);
        // Tạo thông điệp i18n
        String message = messageSource.getMessage(
                "category.delete.success",
                null,
                "Category deleted successfully",
                LocaleContextHolder.getLocale()
        );
        System.out.println("message"+message);
        // Trả về ApiResponseDTO
        ApiResponse response = new ApiResponse();
        response.setCode("DELETE_SUCCESS");
        response.setMessage(message);
        return response;
    }

    @Override
    public CategorySearchResponseDTO searchCategories(String name, String categoryCode, LocalDateTime createdFrom, LocalDateTime createdTo, int page, int size) {
        if (createdFrom != null && createdFrom.isAfter(LocalDateTime.now())) {
            throw new AppException(ErrorCode.CATEGORY_SEARCH_FROM_AFTER_NOW);
        }
        if (createdFrom!= null && createdTo != null) {
            if (createdFrom.isAfter(createdTo)) {
                throw new AppException(ErrorCode.CATEGORY_SREACH_CREATED_NOT_VALID);
            }
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.searchCategories(name, categoryCode, createdFrom, createdTo, pageable);

        // Lấy tất cả ID của category
        List<Long> categoryIds = categoryPage.getContent().stream()
                .map(Category::getId)
                .collect(Collectors.toList());

        // Truy vấn ảnh từ repository -> group theo categoryId (lấy từ entity)
        Map<Long, List<CategoryImageResponseDTO>> imageMap = categoryRepository.findImagesByCategoryIds(categoryIds)
                .stream()
                .filter(image -> image.getCategory() != null && image.getCategory().getId() != null)
                .collect(Collectors.groupingBy(
                        image -> image.getCategory().getId(),
                        Collectors.mapping(categoryImageMapper::toResponseDTO, Collectors.toList())
                ));

        // Mapping từng category -> CategoryResponseDTO + set images
        List<CategoryResponseDTO> data = categoryPage.getContent().stream()
                .map(category -> {
                    CategoryResponseDTO response = categoryMapper.toResponseDTO(category);
                    List<CategoryImageResponseDTO> imagesForCategory = imageMap.getOrDefault(category.getId(), new ArrayList<>());
                    response.setImages(imagesForCategory);
                    return response;
                })
                .collect(Collectors.toList());

        // Tạo phần phân trang
        CategorySearchResponseDTO.PaginationDTO pagination = new CategorySearchResponseDTO.PaginationDTO(
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.hasNext(),
                categoryPage.hasPrevious()
        );
        if (pagination.getTotalElements() == 0){
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return new CategorySearchResponseDTO(data, pagination);
    }

    @Override
    public ByteArrayOutputStream exportCategoriesToExcel(String name, String categoryCode, LocalDateTime createdFrom, LocalDateTime createdTo) {
        if (createdFrom != null && createdFrom.isAfter(LocalDateTime.now())) {
            throw new AppException(ErrorCode.CATEGORY_SEARCH_FROM_AFTER_NOW);
        }
        if (createdFrom!= null && createdTo != null) {
            if (createdFrom.isAfter(createdTo)) {
                throw new AppException(ErrorCode.CATEGORY_SREACH_CREATED_NOT_VALID);
            }
        }
        logger.debug("Exporting categories to Excel with params: name={}, categoryCode={}, createdFrom={}, createdTo={}",
                name, categoryCode, createdFrom, createdTo);

        // Lấy tất cả dữ liệu (không phân trang)
        Pageable pageable = Pageable.unpaged();
        Page<Category> categoryPage = categoryRepository.searchCategories(name, categoryCode, createdFrom, createdTo, pageable);
        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
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
            throw new AppException(ErrorCode.EXPORT_ERROR);
        }
    }

}