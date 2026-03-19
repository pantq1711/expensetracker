package com.anphan.expensetracker.controller;

import com.anphan.expensetracker.dto.CategoryDTO;
import com.anphan.expensetracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    //get all Category
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategory(){
        return ResponseEntity.ok().body(categoryService.getAllCategory());
    }

    //get category by id
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id){
        return ResponseEntity.ok().body(categoryService.getCategoryById(id));
    }

    // update category
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO dto){
        return ResponseEntity.ok().body(categoryService.updateCategory(id, dto));
    }

    // create category
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO dto){
        return ResponseEntity.status(201).body(categoryService.createCategory(dto));
    }

    // delete category
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}