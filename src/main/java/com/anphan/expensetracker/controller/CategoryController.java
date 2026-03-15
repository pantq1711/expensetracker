package com.anphan.expensetracker.controller;

import com.anphan.expensetracker.dto.CategoryDTO;
import com.anphan.expensetracker.entity.Category;
import com.anphan.expensetracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    // get all categories

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategory(){
        return ResponseEntity.ok(categoryService.getAllCategory());
    }

    // get category by id
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id){
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // update category by id
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategoryById(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO){
        return ResponseEntity.ok(categoryService.updateCategoryById(id, categoryDTO));
    }

    // create category
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO){
        return ResponseEntity.status(201).body(categoryService.createCategory(categoryDTO));
    }

    // delete category
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }
}
