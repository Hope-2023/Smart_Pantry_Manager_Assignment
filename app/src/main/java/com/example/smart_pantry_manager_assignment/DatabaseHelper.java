package com.example.smart_pantry_manager_assignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_PANTRY = "pantry";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    // Common Column Names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_UNIT = "unit";

    // Pantry Specific Column
    public static final String KEY_EXPIRY = "expiry_date";

    // Recipe Specific Column
    public static final String KEY_INSTRUCTIONS = "instructions";
    public static final String KEY_RECIPE_ID = "recipe_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PANTRY_TABLE = "CREATE TABLE " + TABLE_PANTRY + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_QUANTITY + " REAL,"
                + KEY_UNIT + " TEXT,"
                + KEY_EXPIRY + " TEXT" + ")";

        String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_INSTRUCTIONS + " TEXT" + ")";

        String CREATE_RECIPE_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_RECIPE_ID + " INTEGER,"
                + KEY_NAME + " TEXT,"
                + KEY_QUANTITY + " REAL,"
                + KEY_UNIT + " TEXT" + ")";

        db.execSQL(CREATE_PANTRY_TABLE);
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_RECIPE_INGREDIENTS_TABLE);

        seedInitialRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        onCreate(db);
    }

    // CRUD: Pantry
    public long addPantryItem(Ingredient item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getName().trim().toLowerCase());
        values.put(KEY_QUANTITY, item.getQuantity());
        values.put(KEY_UNIT, item.getUnit());
        values.put(KEY_EXPIRY, item.getExpiryDate());
        long id = db.insert(TABLE_PANTRY, null, values);
        db.close();
        return id;
    }

    public List<Ingredient> getAllPantryItems() {
        List<Ingredient> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PANTRY, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME));
                double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(KEY_UNIT));
                String expiry = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EXPIRY));
                list.add(new Ingredient(id, name, qty, unit, expiry));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public int updatePantryItem(Ingredient item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getName().trim().toLowerCase());
        values.put(KEY_QUANTITY, item.getQuantity());
        values.put(KEY_UNIT, item.getUnit());
        values.put(KEY_EXPIRY, item.getExpiryDate());
        return db.update(TABLE_PANTRY, values, KEY_ID + " = ?", new String[]{String.valueOf(item.getId())});
    }

    public void deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Recipes Retrieval
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPES, null);

        if (cursor.moveToFirst()) {
            do {
                int recipeId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow(KEY_INSTRUCTIONS));
                List<Ingredient> ingredients = getRecipeIngredients(recipeId);
                recipes.add(new Recipe(recipeId, name, instructions, ingredients));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recipes;
    }

    private List<Ingredient> getRecipeIngredients(int recipeId) {
        List<Ingredient> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPE_INGREDIENTS + " WHERE " + KEY_RECIPE_ID + " = ?", new String[]{String.valueOf(recipeId)});

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME));
                double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(KEY_UNIT));
                list.add(new Ingredient(name, qty, unit, ""));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private void seedInitialRecipes(SQLiteDatabase db) {
        addRecipeWithIngredients(db, "Scrambled Eggs", "Beat eggs with salt. Melt butter in pan and cook gently.",
                new String[]{"egg", "butter"}, new double[]{2, 10}, new String[]{"pcs", "g"});

        addRecipeWithIngredients(db, "Pancakes", "Mix flour, egg, milk, and sugar. Fry on lightly greased pan.",
                new String[]{"flour", "egg", "milk", "sugar"}, new double[]{200, 1, 300, 20}, new String[]{"g", "pcs", "ml", "g"});

        addRecipeWithIngredients(db, "Tomato Soup", "Simmer tomato and onion with garlic and salt. Blend until smooth.",
                new String[]{"tomato", "onion", "garlic", "salt"}, new double[]{4, 1, 2, 5}, new String[]{"pcs", "pcs", "cloves", "g"});

        addRecipeWithIngredients(db, "Garlic Rice", "Sauté garlic in oil. Add cooked rice and salt, toss well.",
                new String[]{"rice", "garlic", "oil", "salt"}, new double[]{200, 3, 15, 2}, new String[]{"g", "cloves", "ml", "g"});

        addRecipeWithIngredients(db, "Cheese Omelette", "Whisk eggs, pour into hot pan, top with cheese, and fold.",
                new String[]{"egg", "cheese", "butter"}, new double[]{3, 50, 10}, new String[]{"pcs", "g", "g"});

        addRecipeWithIngredients(db, "French Toast", "Whisk egg and milk. Dip bread and fry in butter until golden.",
                new String[]{"bread", "egg", "milk", "butter"}, new double[]{2, 1, 50, 10}, new String[]{"slices", "pcs", "ml", "g"});

        addRecipeWithIngredients(db, "Boiled Eggs", "Place eggs in boiling water for 7 minutes. Cool and peel.",
                new String[]{"egg", "water"}, new double[]{2, 500}, new String[]{"pcs", "ml"});

        addRecipeWithIngredients(db, "Guacamole", "Mash avocado with chopped onion, tomato, and salt.",
                new String[]{"avocado", "onion", "tomato", "salt"}, new double[]{2, 1, 1, 2}, new String[]{"pcs", "pcs", "pcs", "g"});

        addRecipeWithIngredients(db, "Fried Rice", "Sauté rice with egg, onion, and soy sauce in oil.",
                new String[]{"rice", "egg", "onion", "oil"}, new double[]{250, 2, 1, 15}, new String[]{"g", "pcs", "pcs", "ml"});

        addRecipeWithIngredients(db, "Butter Toast", "Toast bread and spread butter evenly.",
                new String[]{"bread", "butter"}, new double[]{2, 15}, new String[]{"slices", "g"});

        addRecipeWithIngredients(db, "Mashed Potatoes", "Boil potato, mash with butter, milk, and salt.",
                new String[]{"potato", "butter", "milk", "salt"}, new double[]{3, 20, 50, 3}, new String[]{"pcs", "g", "ml", "g"});

        addRecipeWithIngredients(db, "Sautéed Vegetables", "Sauté chopped potato and onion in oil with salt.",
                new String[]{"potato", "onion", "oil", "salt"}, new double[]{2, 1, 15, 2}, new String[]{"pcs", "pcs", "ml", "g"});

        addRecipeWithIngredients(db, "Milkshake", "Blend milk and sugar together until smooth.",
                new String[]{"milk", "sugar"}, new double[]{300, 25}, new String[]{"ml", "g"});

        addRecipeWithIngredients(db, "Onion Rings", "Coat onion slices in flour batter and deep fry.",
                new String[]{"onion", "flour", "oil", "salt"}, new double[]{2, 100, 100, 2}, new String[]{"pcs", "g", "ml", "g"});

        addRecipeWithIngredients(db, "Garlic Toast", "Toast bread topped with crushed garlic and butter.",
                new String[]{"bread", "garlic", "butter"}, new double[]{2, 2, 15}, new String[]{"slices", "cloves", "g"});
    }

    private void addRecipeWithIngredients(SQLiteDatabase db, String name, String instructions, String[] ingNames, double[] ingQtys, String[] ingUnits) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_INSTRUCTIONS, instructions);
        long recipeId = db.insert(TABLE_RECIPES, null, values);

        for (int i = 0; i < ingNames.length; i++) {
            ContentValues ingValues = new ContentValues();
            ingValues.put(KEY_RECIPE_ID, recipeId);
            ingValues.put(KEY_NAME, ingNames[i].toLowerCase());
            ingValues.put(KEY_QUANTITY, ingQtys[i]);
            ingValues.put(KEY_UNIT, ingUnits[i]);
            db.insert(TABLE_RECIPE_INGREDIENTS, null, ingValues);
        }
    }
}