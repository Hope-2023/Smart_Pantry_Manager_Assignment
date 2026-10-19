package com.example.smart_pantry_manager_assignment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView recipesRecyclerView;
    private TextView emptyRecipesMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dbHelper = new DatabaseHelper(this);
        recipesRecyclerView = findViewById(R.id.recipesRecyclerView);
        emptyRecipesMessage = findViewById(R.id.emptyRecipesMessage);

        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        List<Ingredient> userPantry = dbHelper.getAllPantryItems();
        List<Recipe> allRecipes = dbHelper.getAllRecipes();
        List<Recipe> matchingRecipes = getStrictlyMatchedRecipes(userPantry, allRecipes);

        if (matchingRecipes.isEmpty()) {
            emptyRecipesMessage.setVisibility(View.VISIBLE);
            recipesRecyclerView.setVisibility(View.GONE);
        } else {
            emptyRecipesMessage.setVisibility(View.GONE);
            recipesRecyclerView.setVisibility(View.VISIBLE);
            RecipeAdapter adapter = new RecipeAdapter(this, matchingRecipes);
            recipesRecyclerView.setAdapter(adapter);
        }
    }

    // STRICT MATCHING ALGORITHM
    private List<Recipe> getStrictlyMatchedRecipes(List<Ingredient> pantry, List<Recipe> recipes) {
        List<Recipe> validRecipes = new ArrayList<>();

        for (Recipe recipe : recipes) {
            boolean canMake = true;

            for (Ingredient required : recipe.getIngredients()) {
                boolean foundInPantry = false;

                for (Ingredient pantryItem : pantry) {
                    // Simple string normalization (handles casing and trailing 's' for simple plurals)
                    String pantryName = normalizeName(pantryItem.getName());
                    String reqName = normalizeName(required.getName());

                    if (pantryName.equalsIgnoreCase(reqName)) {
                        if (pantryItem.getQuantity() >= required.getQuantity()) {
                            foundInPantry = true;
                        }
                        break;
                    }
                }

                if (!foundInPantry) {
                    canMake = false;
                    break; // Exclude recipe if even 1 ingredient is missing or insufficient
                }
            }

            if (canMake) {
                validRecipes.add(recipe);
            }
        }
        return validRecipes;
    }

    private String normalizeName(String name) {
        name = name.trim().toLowerCase();
        if (name.endsWith("s") && !name.endsWith("ss")) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }
}