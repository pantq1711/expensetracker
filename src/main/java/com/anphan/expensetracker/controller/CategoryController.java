package com.anphan.expensetracker.controller;

import com.anphan.expensetracker.dto.CategoryDTO;
import com.anphan.expensetracker.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "APIs for managing income/expense categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories of the current user")
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategory(){
        return ResponseEntity.ok().body(categoryService.getAllCategory());
    }

    @Operation(summary = "Get category details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id){
        return ResponseEntity.ok().body(categoryService.getCategoryById(id));
    }

    @Operation(summary = "Update category by ID")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto){
        return ResponseEntity.ok().body(categoryService.updateCategory(id, dto));
    }

    @Operation(summary = "Create a new category")
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO dto){
        return ResponseEntity.status(201).body(categoryService.createCategory(dto));
    }

    @Operation(summary = "Delete category by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}