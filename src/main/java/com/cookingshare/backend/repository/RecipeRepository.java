package com.cookingshare.backend.repository;

import com.cookingshare.backend.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecipeRepository extends MongoRepository<Recipe, String> {
    Page<Recipe> findAllByAuthorId(String authorId, Pageable pageable);
}
