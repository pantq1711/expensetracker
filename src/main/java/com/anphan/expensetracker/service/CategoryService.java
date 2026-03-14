package com.anphan.expensetracker.service;

import com.anphan.expensetracker.dto.CategoryDTO;
import com.anphan.expensetracker.dto.UserDTO;
import com.anphan.expensetracker.entity.Category;
import com.anphan.expensetracker.entity.User;
import com.anphan.expensetracker.exception.ResourceNotFoundException;
import com.anphan.expensetracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    //Lay tat ca category, convert sang DTO
    public List<CategoryDTO> getAllCategory(){
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToDTO) //voi moi category, goi convertToDTO
                .toList();
    }

    //Lay 1 category

    public CategoryDTO getCategoryById(Long id){
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found" + id));
        return convertToDTO(category);
    }

    //Xoa 1 category theo id

    public void deleteCategoryById(Long id){
        if(!categoryRepository.existsById(id)){
            throw new RuntimeException("Category Not Found" + id);
        }
        categoryRepository.deleteById(id);
    }

    // tao moi category

    public CategoryDTO createCategory(CategoryDTO dto){
        Category category = new Category();
        category.setName(dto.getName());
        category.setColorHex(dto.getColorHex());
        category.setType(dto.getType());
        User user = new User();
        user.setId(2L);
        category.setUser(user);
        Category saved = categoryRepository.save(category);
        return convertToDTO(saved);
    }

    //cap nhat
    public CategoryDTO updateCategory(Long id, CategoryDTO dto){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found" + id));
        category.setName(dto.getName());
        category.setType(dto.getType());
        category.setColorHex(dto.getColorHex());
        Category saved = categoryRepository.save(category);
        return convertToDTO(saved);
    }

    //Convert Entity -> DTO( tranh lo thong tin ra ngoai)

    private CategoryDTO convertToDTO(Category category){
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setColorHex(category.getColorHex());
        dto.setName(category.getName());
        dto.setType(category.getType());
        return dto;
    }
}
