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

    //get all category
    public List<CategoryDTO> getAllCategory(){
        return categoryRepository.findAll()
                .stream()
                .map(this :: convertToDTO)
                .toList();
    }

    // get category by Id
    public CategoryDTO getCategoryById(Long id){
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found Category" + id));
        return convertToDTO(category);
    }

    // update category
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO){
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found Category" + id));
        category.setType(categoryDTO.getCategoryType());
        category.setColorHex(categoryDTO.getColorHex());
        category.setName(categoryDTO.getName());
        categoryRepository.save(category);
        return convertToDTO(category);
    }

    // create category
    public CategoryDTO createCategory(CategoryDTO categoryDTO){
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setColorHex(categoryDTO.getColorHex());
        category.setType(categoryDTO.getCategoryType());
        User user = new User();
        user.setId(2L);
        category.setUser(user);
        categoryRepository.save(category);
        return convertToDTO(category);
    }

    // delete category
    public void deleteCategory(Long id){
        if(categoryRepository.findById(id).isEmpty()){
            throw new ResourceNotFoundException("Not found Category");
        }
        categoryRepository.deleteById(id);
    }

    private CategoryDTO convertToDTO(Category category){
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(category.getName());
        categoryDTO.setColorHex(category.getColorHex());
        categoryDTO.setCategoryType(category.getType());
        categoryDTO.setId(category.getId());
        return categoryDTO;
    }
}
