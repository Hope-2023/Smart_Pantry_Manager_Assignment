package com.example.smart_pantry_manager_assignment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText editName, editQty, editUnit, editExpiry;
    private Button saveBtn, deleteBtn;
    private DatabaseHelper dbHelper;
    private int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = new DatabaseHelper(this);

        TextView formTitle = findViewById(R.id.formTitle);
        editName = findViewById(R.id.editIngredientName);
        editQty = findViewById(R.id.editIngredientQuantity);
        editUnit = findViewById(R.id.editIngredientUnit);
        editExpiry = findViewById(R.id.editIngredientExpiry);
        saveBtn = findViewById(R.id.saveIngredientButton);
        deleteBtn = findViewById(R.id.deleteIngredientButton);

        if (getIntent().hasExtra("ID")) {
            ingredientId = getIntent().getIntExtra("ID", -1);
            formTitle.setText("Edit Ingredient");
            editName.setText(getIntent().getStringExtra("NAME"));
            editQty.setText(String.valueOf(getIntent().getDoubleExtra("QUANTITY", 0.0)));
            editUnit.setText(getIntent().getStringExtra("UNIT"));
            editExpiry.setText(getIntent().getStringExtra("EXPIRY"));
            deleteBtn.setVisibility(View.VISIBLE);
        }

        saveBtn.setOnClickListener(v -> saveIngredient());
        deleteBtn.setOnClickListener(v -> {
            if (ingredientId != -1) {
                dbHelper.deletePantryItem(ingredientId);
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveIngredient() {
        String name = editName.getText().toString().trim();
        String qtyStr = editQty.getText().toString().trim();
        String unit = editUnit.getText().toString().trim();
        String expiry = editExpiry.getText().toString().trim();

        if (name.isEmpty() || qtyStr.isEmpty() || unit.isEmpty()) {
            Toast.makeText(this, "Please fill in Name, Quantity, and Unit", Toast.LENGTH_SHORT).show();
            return;
        }

        double qty = Double.parseDouble(qtyStr);
        Ingredient ingredient = new Ingredient(ingredientId, name, qty, unit, expiry);

        if (ingredientId == -1) {
            dbHelper.addPantryItem(ingredient);
            Toast.makeText(this, "Item added to pantry", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.updatePantryItem(ingredient);
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}