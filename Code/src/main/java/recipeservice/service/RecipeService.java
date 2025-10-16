package recipeservice.service;

import java.util.List;
import org.springframework.stereotype.Service;
import recipeservice.dao.RecipeDao;
import recipeservice.dto.CuisineDto;
import recipeservice.dto.IngredientDto;
import recipeservice.dto.RecipeDto;
import recipeservice.dto.ReviewDto;
import recipeservice.exception.CustomException;
import recipeservice.model.Cuisine;
import recipeservice.model.Ingredient;
import recipeservice.model.Recipe;

@Service
public class RecipeService {

    private final RecipeDao recipeDao;
    private final CacheService cacheService;

    public RecipeService(RecipeDao recipeDao, CacheService cacheService) {
        this.recipeDao = recipeDao;
        this.cacheService = cacheService;
    }

    public List<RecipeDto> getAllRecipes() {
        List<Recipe> recipes = recipeDao.getAllRecipes();
        return recipes.stream()
                .map(recipe -> {
                    List<IngredientDto> ingredientDtos = (recipe.getIngredients() != null)
                            ? recipe.getIngredients().stream()
                            .map(ingredient ->
                                    new IngredientDto(ingredient.getId(), ingredient.getName()))
                            .toList() : List.of();

                    List<ReviewDto> reviewDtos = (recipe.getReviews() != null)
                            ? recipe.getReviews().stream()
                            .map(review ->
                                    new ReviewDto(review.getId(),
                                            review.getText(),
                                            review.getRating(),
                                            recipe.getId()))
                            .toList() : List.of();

                    Cuisine cuisine = recipe.getCuisine();
                    CuisineDto cuisineDto = (cuisine != null)
                            ? new CuisineDto(cuisine.getId(), cuisine.getName())
                            : null;

                    return new RecipeDto(
                            recipe.getId(),
                            recipe.getTitle(),
                            recipe.getDescription(),
                            ingredientDtos,
                            reviewDtos,
                            cuisineDto
                    );
                })
                .toList();
    }

    public RecipeDto getRecipeById(Long id) {
        Recipe recipe = recipeDao.getRecipeById(id);
        return recipe != null ? convertToDto(recipe) : null;
    }

    public RecipeDto createRecipe(RecipeDto recipeDto) {
        if (recipeDto == null || recipeDto.getTitle() == null
                || recipeDto.getDescription() == null) {
            throw new CustomException("Название и описание не могут быть пустыми");
        }

        Recipe recipe = convertToEntity(recipeDto);
        Recipe createdRecipe = recipeDao.createRecipe(recipe);

        if (createdRecipe != null && createdRecipe.getCuisine() != null) {
            String cacheKey = createdRecipe.getCuisine().getName();
            if (cacheKey != null) {
                cacheService.updateCache(cacheKey, convertToDto(createdRecipe));
            }
        }

        return convertToDto(createdRecipe);
    }

    public RecipeDto updateRecipe(Long id, RecipeDto recipeDto) {
        Recipe existingRecipe = recipeDao.getRecipeById(id);
        if (existingRecipe == null) {
            return null;
        }

        Recipe recipe = convertToEntity(recipeDto);
        recipe.setId(id);
        Recipe updatedRecipe = recipeDao.updateRecipe(id, recipe);
        return convertToDto(updatedRecipe);
    }

    public void deleteRecipe(Long id) {
        Recipe recipe = recipeDao.getRecipeById(id);
        if (recipe != null) {
            String cacheKey = recipe.getCuisine().getName();

            recipeDao.deleteRecipe(id);
            cacheService.removeCachedRecipes(cacheKey);
        }
    }

    private RecipeDto convertToDto(Recipe recipe) {
        List<IngredientDto> ingredientDtos = (recipe.getIngredients() != null)
                ? recipe.getIngredients().stream()
                .map(ingredient -> new IngredientDto(
                        ingredient.getId(),
                        ingredient.getName()))
                .toList() : List.of();

        List<ReviewDto> reviewDtos = (recipe.getReviews() != null)
                ? recipe.getReviews().stream()
                .map(review -> new ReviewDto(
                        review.getId(),
                        review.getText(),
                        review.getRating(),
                        recipe.getId()))
                .toList() : List.of();

        Cuisine cuisine = recipe.getCuisine();
        CuisineDto cuisineDto = (cuisine != null)
                ? new CuisineDto(cuisine.getId(), cuisine.getName())
                : null;

        return new RecipeDto(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                ingredientDtos,
                reviewDtos,
                cuisineDto
        );
    }

    private Recipe convertToEntity(RecipeDto recipeDto) {
        Recipe recipe = new Recipe();
        recipe.setTitle(recipeDto.getTitle());
        recipe.setDescription(recipeDto.getDescription());

        if (recipeDto.getCuisine() != null) {
            Cuisine cuisine = new Cuisine();
            cuisine.setId(recipeDto.getCuisine().getId());
            recipe.setCuisine(cuisine);
        }

        List<Ingredient> ingredients = (recipeDto.getIngredients() != null)
                ? recipeDto.getIngredients().stream()
                .map(ingredientDto -> {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(ingredientDto.getId());
                    return ingredient;
                })
                .toList() : List.of();

        recipe.setIngredients(ingredients);
        return recipe;
    }

    public List<RecipeDto> getRecipesByCuisineName(String cuisineName) {
        List<RecipeDto> cachedRecipes = cacheService.getCachedRecipes(cuisineName);
        if (cachedRecipes != null) {
            return cachedRecipes;
        }

        List<Recipe> recipes = recipeDao.findRecipesByCuisineNameJpql(cuisineName);
        if (recipes.isEmpty()) {
            recipes = recipeDao.findRecipesByCuisineNameNative(cuisineName);
        }

        List<RecipeDto> recipeDtos = recipes.stream().map(this::convertToDto).toList();

        if (!recipeDtos.isEmpty()) {
            cacheService.cacheRecipes(cuisineName, recipeDtos);
        }

        return recipeDtos;
    }
}