package com.example.smart_pantry_manager_assignment;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView titleView = findViewById(R.id.detailRecipeTitle);
        TextView ingredientsView = findViewById(R.id.detailRecipeIngredients);
        TextView instructionsView = findViewById(R.id.detailRecipeInstructions);

        String title = getIntent().getStringExtra("RECIPE_NAME");
        String ingredients = getIntent().getStringExtra("RECIPE_INGREDIENTS");
        String instructions = getIntent().getStringExtra("RECIPE_INSTRUCTIONS");

        titleView.setText(title);
        ingredientsView.setText(ingredients);
        instructionsView.setText(instructions);
    }
}