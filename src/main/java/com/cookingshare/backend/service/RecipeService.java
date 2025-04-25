package com.cookingshare.backend.service;

import com.cookingshare.backend.dto.RecipeRequest;
import com.cookingshare.backend.dto.RecipeResponse;
import com.cookingshare.backend.dto.UserDTO;
import com.cookingshare.backend.model.Recipe;
import com.cookingshare.backend.repository.RecipeRepository;
import com.cookingshare.backend.repository.UserRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository repo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StorageService storage;

    /** Create a new recipe with images and authorId */
    public RecipeResponse create(String authorId,
                                 RecipeRequest req,
                                 MultipartFile[] images) {
        Recipe r = new Recipe();
        r.setAuthorId(authorId);
        r.setTitle(req.getTitle());
        r.setDescription(req.getDescription());
        r.setIngredients(req.getIngredients());
        r.setSteps(req.getSteps());
        r.setTags(req.getTags());

        List<String> urls = new ArrayList<>();
        for (MultipartFile img : images) {
            if (!img.isEmpty()) {
                urls.add(storage.upload(img));
            }
        }
        r.setImageUrls(urls);

        Recipe saved = repo.save(r);
        return toDto(saved);
    }

    /** Global feed, paginated and sorted by creation date desc */
    public Page<RecipeResponse> feed(int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repo.findAll(p).map(this::toDto);
    }

    /** Find a single recipe by its ID */
    public Optional<RecipeResponse> findById(String id) {
        return repo.findById(id).map(this::toDto);
    }

    /** Find recipes by a given author (user) ID */
    public Page<RecipeResponse> findByAuthor(String authorId, int page, int size) {
        Pageable pg = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repo.findAllByAuthorId(authorId, pg).map(this::toDto);
    }
    /** Internal helper: map Recipe → RecipeResponse including author info */

    /** Update an existing recipe’s fields */
    public Optional<RecipeResponse> update(
            String id,
            String authorId,
            RecipeRequest req,
            MultipartFile[] images
    ) {
        return repo.findById(id)
                   // only the creator can update:
                   .filter(r -> r.getAuthorId().equals(authorId))
                   .map(recipe -> {
                       // apply updates…
                       recipe.setTitle(req.getTitle());
                       recipe.setDescription(req.getDescription());
                       recipe.setIngredients(req.getIngredients());
                       recipe.setSteps(req.getSteps());
                       recipe.setTags(req.getTags());
                       if (images != null && images.length > 0) {
                           List<String> urls = new ArrayList<>();
                           for (MultipartFile img : images) {
                               if (!img.isEmpty()) {
                                   urls.add(storage.upload(img));
                               }
                           }
                           recipe.setImageUrls(urls);
                       }
                       recipe.setUpdatedAt(new Date());
                       Recipe saved = repo.save(recipe);
                       return toDto(saved);
                   });
    }

    private RecipeResponse toDto(Recipe r) {
        RecipeResponse dto = new RecipeResponse();
        BeanUtils.copyProperties(r, dto);

        // Enrich with full author info:
        userRepository.findById(r.getAuthorId()).ifPresent(user -> {
            UserDTO ud = new UserDTO(user.getId(), user.getName(), user.getEmail());
            dto.setAuthor(ud);
        });

        return dto;
    }

   
    /**
     * Deletes a recipe if it exists and belongs to the given author.
     *
     * @param id       the recipe’s ID
     * @param authorId the user ID from the JWT
     * @return true if the recipe was found & deleted, false otherwise
     */
    public boolean delete(String id, String authorId) {
        return repo.findById(id)
                   // enforce “only the creator can delete”
                   .filter(r -> r.getAuthorId().equals(authorId))
                   .map(r -> {
                       repo.deleteById(id);
                       return true;
                   })
                   .orElse(false);
    }



}
