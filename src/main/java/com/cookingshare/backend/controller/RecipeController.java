package com.cookingshare.backend.controller;

import com.cookingshare.backend.dto.RecipeRequest;
import com.cookingshare.backend.dto.RecipeResponse;
import com.cookingshare.backend.dto.ResponseMessageDTO;
import com.cookingshare.backend.security.JwtUtil;
import com.cookingshare.backend.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired private RecipeService service;
    @Autowired private JwtUtil jwtUtil;

   
 
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecipeResponse> createRecipe(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam String title,
        @RequestParam String description,
        @RequestParam List<String> ingredients,
        @RequestParam List<String> steps,
        @RequestParam List<String> tags,
        @RequestPart("images") MultipartFile[] images
    ) {
        // build a RecipeRequest yourself or just call the service directly
        RecipeRequest req = new RecipeRequest();
        req.setTitle(title);
        req.setDescription(description);
        req.setIngredients(ingredients);
        req.setSteps(steps);
        req.setTags(tags);

        String token  = authHeader.replace("Bearer ", "");
        String authorId = jwtUtil.getUserIdFromToken(token);

        RecipeResponse resp = service.create(authorId, req, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping
    public ResponseEntity<Page<RecipeResponse>> getFeed(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size) {
        return ResponseEntity.ok(service.feed(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipe(@PathVariable String id) {
        Optional<RecipeResponse> opt = service.findById(id);
        return opt.<ResponseEntity<?>>map(ResponseEntity::ok)
                  .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body("Recipe not found"));
    }
    /** Get recipes by arbitrary user ID */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<RecipeResponse>> getByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.findByAuthor(userId, page, size));
    }

    //update recipe
    @PutMapping(
        value    = "/{id}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
      )
      public ResponseEntity<?> updateRecipe(
          @PathVariable String id,
          @RequestHeader("Authorization") String authHeader,
          @RequestParam String title,
          @RequestParam String description,
          @RequestParam List<String> ingredients,
          @RequestParam List<String> steps,
          @RequestParam List<String> tags,
          @RequestPart(value = "images", required = false) MultipartFile[] images
      ) {
          // build request DTO
          RecipeRequest req = new RecipeRequest();
          req.setTitle(title);
          req.setDescription(description);
          req.setIngredients(ingredients);
          req.setSteps(steps);
          req.setTags(tags);
  
          // extract and validate user
          String authorId = jwtUtil.getUserIdFromToken(authHeader.replace("Bearer ",""));
  
          Optional<RecipeResponse> updated = service.update(id, authorId, req, images);
  
          if (updated.isPresent()) {
              return ResponseEntity.ok(updated.get());
          } else {
              // either not found or not owner → 404
              return ResponseEntity
                     .status(HttpStatus.NOT_FOUND)
                     .body(new ResponseMessageDTO("Recipe not found or not allowed"));
          }
      }

      //delete recipe , only alowed for the author
      @DeleteMapping("/{id}")
      public ResponseEntity<?> deleteRecipe(
          @PathVariable String id,
          @RequestHeader("Authorization") String authHeader
      ) {
          String authorId = jwtUtil.getUserIdFromToken(authHeader.replace("Bearer ", ""));
          boolean deleted = service.delete(id, authorId);
          if (deleted) {
              return ResponseEntity.ok(new ResponseMessageDTO("Recipe deleted"));
          } else {
              return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                   .body(new ResponseMessageDTO("Recipe not found or not allowed"));
          }
      }
      

    
}
