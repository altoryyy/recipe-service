package recipeservice.dao;


import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import recipeservice.model.Recipe;

public interface RecipeDao {
    List<Recipe> getAllRecipes();

    List<Recipe> findRecipesByIngredientId(Long ingredientId);

    Recipe getRecipeById(Long id);

    Recipe createRecipe(Recipe recipe);

    Recipe updateRecipe(Long id, Recipe recipe);

    void deleteRecipe(Long id);

    @Query("SELECT r FROM Recipe r JOIN r.cuisine c WHERE c.name = :cuisineName")
    List<Recipe> findRecipesByCuisineNameJpql(@Param("cuisineName") String cuisineName);

    @Query(value =
            "SELECT r.* FROM recipe r JOIN cuisine c "
                    + "ON r.cuisine_id = c.id WHERE c.name = :cuisineName",
            nativeQuery = true)
    List<Recipe> findRecipesByCuisineNameNative(@Param("cuisineName") String cuisineName);
}