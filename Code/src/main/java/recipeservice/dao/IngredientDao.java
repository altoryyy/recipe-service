package recipeservice.dao;

import java.util.List;
import recipeservice.model.Ingredient;

public interface IngredientDao {
    List<Ingredient> getAllIngredients();

    Ingredient getIngredientById(Long id);

    Ingredient createIngredient(Ingredient ingredient);

    Ingredient updateIngredient(Long id, Ingredient ingredient);

    void deleteIngredient(Long id);
}