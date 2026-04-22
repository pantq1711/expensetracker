    package com.anphan.expensetracker.service;

    import com.anphan.expensetracker.dto.CategoryDTO;
    import com.anphan.expensetracker.dto.UserDTO;
    import com.anphan.expensetracker.entity.Category;
    import com.anphan.expensetracker.entity.User;
    import com.anphan.expensetracker.exception.ResourceNotFoundException;
    import com.anphan.expensetracker.repository.CategoryRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import org.springframework.web.bind.annotation.RequestBody;

    import java.util.List;

    @Service
    @RequiredArgsConstructor
    public class CategoryService {
        private final CategoryRepository categoryRepository;
        private final com.anphan.expensetracker.util.SecurityUtils securityUtils;

        private Category getCategoryAndCheckOwnership(Long id){
            Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục: " + id));

            User currentUser = securityUtils.getCurrentUser();

            if(!category.getUser().getId().equals(currentUser.getId())){
                throw new RuntimeException("Bạn không có quyền truy cập");
            }

            return category;
        }

        public List<CategoryDTO> getAllCategory(){
            return categoryRepository.findAll()
                    .stream()
                    .map(this :: convertToDTO)
                    .toList();
        }

        public CategoryDTO getCategoryById(Long id){
            return convertToDTO(getCategoryAndCheckOwnership(id));
        }

        public CategoryDTO updateCategory(Long id, CategoryDTO dto){
            Category category = getCategoryAndCheckOwnership(id);
            category.setName(dto.getName());
            category.setColorHex(dto.getColorHex());
            category.setType(dto.getCategoryType());
            categoryRepository.save(category);
            return convertToDTO(category);
        }

        public CategoryDTO createCategory(CategoryDTO dto){
            Category category = new Category();
            User user = securityUtils.getCurrentUser();
            category.setName(dto.getName());
            category.setUser(user);
            category.setType(dto.getCategoryType());
            category.setColorHex(dto.getColorHex());
            categoryRepository.save(category);
            return convertToDTO(category);
        }

        public void deleteCategory(Long id){
            categoryRepository.delete(getCategoryAndCheckOwnership(id));
        }

        private CategoryDTO convertToDTO(Category category){
            CategoryDTO dto = new CategoryDTO();
            dto.setId(category.getId());
            dto.setCategoryType(category.getType());
            dto.setName(category.getName());
            dto.setColorHex(category.getColorHex());
            return dto;
        }
    }