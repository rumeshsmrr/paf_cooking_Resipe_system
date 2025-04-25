package com.cookingshare.backend.dto;

import java.util.List;

public class RecipeRequest {
    private String title;
    private String description;
    private List<String> ingredients;
    private List<String> steps;
    private List<String> tags;

    public RecipeRequest() {}

    // -- getters & setters --
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
