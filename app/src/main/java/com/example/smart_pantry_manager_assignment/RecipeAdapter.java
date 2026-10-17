package com.example.smart_pantry_manager_assignment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final Context context;
    private final List<Recipe> recipeList;

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.text1.setText(recipe.getName());
        holder.text2.setText(recipe.getIngredients().size() + " Ingredients Required");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("RECIPE_NAME", recipe.getName());
            intent.putExtra("RECIPE_INSTRUCTIONS", recipe.getInstructions());
            StringBuilder sb = new StringBuilder();
            for (Ingredient ing : recipe.getIngredients()) {
                sb.append("• ").append(ing.getName()).append(" (").append(ing.getQuantity()).append(" ").append(ing.getUnit()).append(")\n");
            }
            intent.putExtra("RECIPE_INGREDIENTS", sb.toString());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return recipeList.size(); }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;
        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}