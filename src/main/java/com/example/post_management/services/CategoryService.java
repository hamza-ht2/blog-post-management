package com.example.post_management.services;

import com.example.post_management.models.Category;
import com.example.post_management.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(Category category){
        if (categoryRepository.existsByName(category.getName())){
            throw new RuntimeException("category already exists");
        }
        return categoryRepository.save(category);
    }
    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }
    public Category getCategoryById(Long categoryId){
        return categoryRepository.findById(categoryId).orElseThrow(()-> new RuntimeException("category not found with id : "+categoryId));
    }
    public Category getCategoryByName(String name){
        return categoryRepository.findCategoryByName(name).orElseThrow(()-> new RuntimeException("category not found with this name :"+name));
    }
    public Category updateCategory(Long categoryId, Category categoryUpdated){
        Category existing = getCategoryById(categoryId);
        if (categoryUpdated.getName() != null) existing.setName(categoryUpdated.getName());
        if (categoryUpdated.getDescription() != null) existing.setDescription(categoryUpdated.getDescription());
        return categoryRepository.save(existing);
    }
    public void deleteCategory(Long categoryId){
        categoryRepository.deleteById(categoryId);
    }
}
