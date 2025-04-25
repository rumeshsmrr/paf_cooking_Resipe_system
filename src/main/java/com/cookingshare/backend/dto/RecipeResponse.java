package com.cookingshare.backend.dto;

import java.util.Date;
import java.util.List;

public class RecipeResponse {
    private String id;
    private String authorId;
    private UserDTO author;           // ← must have getter & setter

    private String title;
    private String description;
    private List<String> ingredients;
    private List<String> steps;
    private List<String> imageUrls;
    private List<String> tags;
    private int likeCount;
    private Date createdAt;
    private Date updatedAt;


    public RecipeResponse() {}

    // -- getters & setters --
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getAuthorId() { return authorId; }
    public void   setAuthorId(String authorId) { this.authorId = authorId; }

    public UserDTO getAuthor() { return author; }
    public void    setAuthor(UserDTO author) { this.author = author; }
}
