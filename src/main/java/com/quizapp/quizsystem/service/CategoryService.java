package com.quizapp.quizsystem.service;

import com.quizapp.quizsystem.dto.category.CategoryRequest;
import com.quizapp.quizsystem.dto.category.CategoryResponse;
import com.quizapp.quizsystem.entity.Category;
import com.quizapp.quizsystem.exception.BadRequestException;
import com.quizapp.quizsystem.exception.ResourceNotFoundException;
import com.quizapp.quizsystem.repository.CategoryRepository;
import com.quizapp.quizsystem.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException("A category with this name already exists");
        }
        Category saved = categoryRepository.save(
                Category.builder().name(request.getName()).description(request.getDescription()).build()
        );
        return toResponse(saved);
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return toResponse(categoryRepository.save(category));
    }

    /**
     * Deletion is only allowed when no quiz references this category
     * (Section 4: "delete categories where deletion is safe/allowed").
     * Otherwise, deleting the category would orphan quizzes or force a
     * cascading delete of content an admin probably didn't intend to lose.
     */
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        boolean inUse = !quizRepository.findByCategoryId(id).isEmpty();
        if (inUse) {
            throw new BadRequestException(
                    "Cannot delete category '" + category.getName() + "' — quizzes are still assigned to it");
        }
        categoryRepository.delete(category);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }
}
