package recipeservice.service;

import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;
import recipeservice.dao.IngredientDao;
import recipeservice.dao.RecipeDao;
import recipeservice.dto.IngredientDto;
import recipeservice.model.Ingredient;
import recipeservice.model.Recipe;

@Service
public class IngredientService {

    private final IngredientDao ingredientDao;
    private final RecipeDao recipeDao;

    public IngredientService(IngredientDao ingredientDao, RecipeDao recipeDao) {
        this.ingredientDao = ingredientDao;
        this.recipeDao = recipeDao;
    }

    public List<IngredientDto> getAllIngredients() {
        List<Ingredient> ingredients = ingredientDao.getAllIngredients();
        return ingredients.stream()
                .map(this::convertToDto)
                .toList();
    }

    public IngredientDto getIngredientById(Long id) {
        Ingredient ingredient = ingredientDao.getIngredientById(id);
        return ingredient != null ? convertToDto(ingredient) : null;
    }

    public IngredientDto createIngredient(IngredientDto ingredientDto) {
        if (ingredientDto == null) {
            return null;
        }
        Ingredient ingredient = convertToEntity(ingredientDto);
        Ingredient createdIngredient = ingredientDao.createIngredient(ingredient);
        return convertToDto(createdIngredient);
    }

    public IngredientDto updateIngredient(Long id, IngredientDto ingredientDto) {
        if (ingredientDto == null) {
            return null;
        }
        Ingredient ingredient = convertToEntity(ingredientDto);
        ingredient.setId(id);
        Ingredient updatedIngredient = ingredientDao.updateIngredient(id, ingredient);
        return updatedIngredient != null ? convertToDto(updatedIngredient) : null;
    }

    @Transactional
    public void deleteIngredient(Long id) {
        List<Recipe> recipes = recipeDao.findRecipesByIngredientId(id);
        for (Recipe recipe : recipes) {
            List<Ingredient> ingredients = recipe.getIngredients();
            if (ingredients != null) {
                ingredients.removeIf(ingredient -> ingredient.getId().equals(id));
            }
        }
        ingredientDao.deleteIngredient(id);
    }

    private IngredientDto convertToDto(Ingredient ingredient) {
        return new IngredientDto(ingredient.getId(), ingredient.getName());
    }

    private Ingredient convertToEntity(IngredientDto ingredientDto) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientDto.getName());
        return ingredient;
    }
}