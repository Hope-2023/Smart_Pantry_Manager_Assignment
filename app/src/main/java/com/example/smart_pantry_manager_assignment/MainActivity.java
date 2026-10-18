package com.example.smart_pantry_manager_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView pantryListView;
    private TextView emptyPantryMessage;
    private List<Ingredient> pantryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        pantryListView = findViewById(R.id.pantryListView);
        emptyPantryMessage = findViewById(R.id.emptyPantryMessage);

        Button addBtn = findViewById(R.id.addIngredientButton);
        Button recipesBtn = findViewById(R.id.suggestRecipesButton);
        Button settingsBtn = findViewById(R.id.settingsButton);

        addBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddEditIngredientActivity.class)));
        recipesBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SuggestedRecipesActivity.class)));
        settingsBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        pantryListView.setOnItemClickListener((parent, view, position, id) -> {
            Ingredient selected = pantryList.get(position);
            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            intent.putExtra("ID", selected.getId());
            intent.putExtra("NAME", selected.getName());
            intent.putExtra("QUANTITY", selected.getQuantity());
            intent.putExtra("UNIT", selected.getUnit());
            intent.putExtra("EXPIRY", selected.getExpiryDate());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryData();
    }

    private void loadPantryData() {
        pantryList = dbHelper.getAllPantryItems();
        if (pantryList.isEmpty()) {
            emptyPantryMessage.setVisibility(View.VISIBLE);
            pantryListView.setVisibility(View.GONE);
        } else {
            emptyPantryMessage.setVisibility(View.GONE);
            pantryListView.setVisibility(View.VISIBLE);

            List<String> displayStrings = new ArrayList<>();
            for (Ingredient item : pantryList) {
                displayStrings.add(item.getName().substring(0, 1).toUpperCase() + item.getName().substring(1)
                        + " - " + item.getQuantity() + " " + item.getUnit()
                        + (item.getExpiryDate().isEmpty() ? "" : " (Exp: " + item.getExpiryDate() + ")"));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayStrings);
            pantryListView.setAdapter(adapter);
        }
    }
}