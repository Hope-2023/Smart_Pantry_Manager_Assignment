package com.example.smart_pantry_manager_assignment;

import java.util.List;

public class Recipe {
    private int id;
    private String name;
    private String instructions;
    private List<Ingredient> ingredients;

    public Recipe(int id, String name, String instructions, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getInstructions() { return instructions; }
    public List<Ingredient> getIngredients() { return ingredients; }
}