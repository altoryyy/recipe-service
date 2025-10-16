package recipeservice.sevice;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import recipeservice.dao.RecipeDao;
import recipeservice.dto.RecipeDto;
import recipeservice.service.RecipeService;
import recipeservice.service.CacheService;
import recipeservice.exception.CustomException;
import recipeservice.model.Cuisine;
import recipeservice.model.Recipe;
import recipeservice.dto.CuisineDto;
import recipeservice.dto.IngredientDto;
import recipeservice.dto.ReviewDto;
import recipeservice.model.Ingredient;
import recipeservice.model.Review;
import java.util.ArrayList;
import java.util.List;

public class RecipeServiceTest {

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeDao recipeDao;

    @Mock
    private CacheService cacheService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateRecipe_Success() {
        RecipeDto recipeDto = new RecipeDto(1L, "Test Recipe", "Description", new ArrayList<>(), new ArrayList<>(), null);
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        when(recipeDao.createRecipe(any())).thenReturn(recipe);
        doNothing().when(cacheService).updateCache(any(), any());

        RecipeDto createdRecipe = recipeService.createRecipe(recipeDto);

        assertNotNull(createdRecipe);
        assertEquals(recipe.getId(), createdRecipe.getId());
        verify(recipeDao, times(1)).createRecipe(any());
    }

    @Test
    public void testGetAllRecipes() {
        Recipe recipe = new Recipe();
        List<Recipe> recipes = List.of(recipe);

        when(recipeDao.getAllRecipes()).thenReturn(recipes);

        List<RecipeDto> recipeDtos = recipeService.getAllRecipes();

        assertNotNull(recipeDtos);
        assertEquals(1, recipeDtos.size());
        verify(recipeDao, times(1)).getAllRecipes();
    }

    @Test
    public void testGetRecipeById_Success() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        when(recipeDao.getRecipeById(1L)).thenReturn(recipe);

        RecipeDto recipeDto = recipeService.getRecipeById(1L);

        assertNotNull(recipeDto);
        assertEquals(recipe.getId(), recipeDto.getId());
        verify(recipeDao, times(1)).getRecipeById(1L);
    }

    @Test
    public void testGetRecipeById_NotFound() {
        when(recipeDao.getRecipeById(1L)).thenReturn(null);

        RecipeDto recipeDto = recipeService.getRecipeById(1L);
        assertNull(recipeDto);
    }

    @Test
    public void testCreateRecipe_InvalidData_ThrowsException() {
        RecipeDto recipeDto = new RecipeDto(null, null, null, null, null, null);

        Exception exception = assertThrows(CustomException.class, () -> {
            recipeService.createRecipe(recipeDto);
        });

        assertEquals("Название и описание не могут быть пустыми", exception.getMessage());
    }

    @Test
    public void testDeleteRecipe_Success() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setCuisine(new Cuisine());

        when(recipeDao.getRecipeById(1L)).thenReturn(recipe);
        doNothing().when(recipeDao).deleteRecipe(1L);

        recipeService.deleteRecipe(1L);

        verify(recipeDao, times(1)).deleteRecipe(1L);
        verify(cacheService, times(1)).removeCachedRecipes(any());
    }

    @Test
    public void testDeleteRecipe_NotFound() {
        when(recipeDao.getRecipeById(1L)).thenReturn(null);

        recipeService.deleteRecipe(1L);

        verify(recipeDao, never()).deleteRecipe(any());
        verify(cacheService, never()).removeCachedRecipes(any());
    }

    @Test
    public void testUpdateRecipe_Success() {
        RecipeDto recipeDto = new RecipeDto(1L, "Updated Recipe", "Updated Description", new ArrayList<>(), new ArrayList<>(), null);
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        when(recipeDao.getRecipeById(1L)).thenReturn(recipe);
        when(recipeDao.updateRecipe(eq(1L), any())).thenReturn(recipe);

        RecipeDto updatedRecipe = recipeService.updateRecipe(1L, recipeDto);

        assertNotNull(updatedRecipe);
        assertEquals(recipe.getId(), updatedRecipe.getId());
        verify(recipeDao, times(1)).updateRecipe(eq(1L), any());
    }

    @Test
    public void testUpdateRecipe_NotFound() {
        RecipeDto recipeDto = new RecipeDto(1L, "Updated Recipe", "Updated Description", new ArrayList<>(), new ArrayList<>(), null);
        when(recipeDao.getRecipeById(1L)).thenReturn(null);

        RecipeDto updatedRecipe = recipeService.updateRecipe(1L, recipeDto);

        assertNull(updatedRecipe);
        verify(recipeDao, never()).updateRecipe(any(), any());
    }

    @Test
    public void testGetRecipesByCuisineName_CachedRecipes() {
        String cuisineName = "Italian";
        List<RecipeDto> cachedRecipes = List.of(new RecipeDto(1L, "Pasta", "Delicious pasta recipe", new ArrayList<>(), new ArrayList<>(), null));

        when(cacheService.getCachedRecipes(cuisineName)).thenReturn(cachedRecipes);

        List<RecipeDto> result = recipeService.getRecipesByCuisineName(cuisineName);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cachedRecipes.get(0).getId(), result.get(0).getId());
        verify(recipeDao, never()).findRecipesByCuisineNameJpql(any());
        verify(recipeDao, never()).findRecipesByCuisineNameNative(any());
    }

    @Test
    public void testGetRecipesByCuisineName_NoCachedRecipes() {
        String cuisineName = "Italian";
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Pasta");
        recipe.setDescription("Delicious pasta recipe");
        List<Recipe> recipes = List.of(recipe);

        when(cacheService.getCachedRecipes(cuisineName)).thenReturn(null);
        when(recipeDao.findRecipesByCuisineNameJpql(cuisineName)).thenReturn(recipes);

        List<RecipeDto> result = recipeService.getRecipesByCuisineName(cuisineName);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(recipe.getId(), result.get(0).getId());
        verify(recipeDao, times(1)).findRecipesByCuisineNameJpql(cuisineName);
        verify(recipeDao, never()).findRecipesByCuisineNameNative(any());
        verify(cacheService, times(1)).cacheRecipes(cuisineName, result);
    }

    @Test
    public void testGetRecipesByCuisineName_EmptyResults() {
        String cuisineName = "Italian";
        when(cacheService.getCachedRecipes(cuisineName)).thenReturn(null);
        when(recipeDao.findRecipesByCuisineNameJpql(cuisineName)).thenReturn(new ArrayList<>());

        when(recipeDao.findRecipesByCuisineNameNative(cuisineName)).thenReturn(new ArrayList<>());

        List<RecipeDto> result = recipeService.getRecipesByCuisineName(cuisineName);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeDao, times(1)).findRecipesByCuisineNameJpql(cuisineName);
        verify(recipeDao, times(1)).findRecipesByCuisineNameNative(cuisineName);
        verify(cacheService, never()).cacheRecipes(any(), any()); // Проверяем, что кэширование не вызывалось
    }

    @Test
    public void testGetRecipeById_NegativeId() {
        RecipeDto recipeDto = recipeService.getRecipeById(-1L);
        assertNull(recipeDto); // Ожидаем null
    }

    @Test
    public void testUpdateRecipe_InvalidId() {
        RecipeDto recipeDto = new RecipeDto(1L, "Updated Recipe", "Updated Description", new ArrayList<>(), new ArrayList<>(), null);
        when(recipeDao.getRecipeById(1L)).thenReturn(null);

        RecipeDto updatedRecipe = recipeService.updateRecipe(1L, recipeDto);
        assertNull(updatedRecipe);
    }

    @Test
    public void testGetRecipesByCuisineName_NoResults() {
        String cuisineName = "Italian";
        when(cacheService.getCachedRecipes(cuisineName)).thenReturn(null);
        when(recipeDao.findRecipesByCuisineNameJpql(cuisineName)).thenReturn(new ArrayList<>());
        when(recipeDao.findRecipesByCuisineNameNative(cuisineName)).thenReturn(new ArrayList<>());

        List<RecipeDto> result = recipeService.getRecipesByCuisineName(cuisineName);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cacheService, never()).cacheRecipes(any(), any());
    }

    @Test
    public void testCreateRecipe_WithCuisine() {
        RecipeDto recipeDto = new RecipeDto(1L, "Test Recipe", "Description", new ArrayList<>(), new ArrayList<>(), new CuisineDto(1L, "Italian"));
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        Cuisine cuisine = new Cuisine();
        cuisine.setId(1L);
        cuisine.setName("Italian");
        recipe.setCuisine(cuisine);

        when(recipeDao.createRecipe(any())).thenReturn(recipe);
        doNothing().when(cacheService).updateCache(any(), any());

        RecipeDto createdRecipe = recipeService.createRecipe(recipeDto);

        assertNotNull(createdRecipe);
        assertEquals(recipe.getId(), createdRecipe.getId());
        verify(cacheService, times(1)).updateCache(any(), any());
    }

    @Test
    public void testGetAllRecipes_WithReviewsAndIngredients() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Test Recipe");
        recipe.setDescription("Description");

        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Salt");
        recipe.setIngredients(List.of(ingredient));

        Review review = new Review();
        review.setId(1L);
        review.setText("Delicious!");
        review.setRating(5);
        recipe.setReviews(List.of(review));

        List<Recipe> recipes = List.of(recipe);
        when(recipeDao.getAllRecipes()).thenReturn(recipes);

        List<RecipeDto> recipeDtos = recipeService.getAllRecipes();

        assertNotNull(recipeDtos);
        assertEquals(1, recipeDtos.size());
        assertEquals(1, recipeDtos.get(0).getIngredients().size());
        assertEquals(1, recipeDtos.get(0).getReviews().size());
        assertEquals("Salt", recipeDtos.get(0).getIngredients().get(0).getName());
        assertEquals("Delicious!", recipeDtos.get(0).getReviews().get(0).getText());
    }

    @Test
    public void testCreateRecipe_WithIngredientsAndReviews() {
        RecipeDto recipeDto = new RecipeDto(1L, "Test Recipe", "Description",
                List.of(new IngredientDto(1L, "Salt")),
                List.of(new ReviewDto(1L, "Delicious!", 5, 1L)), null);

        Recipe recipe = new Recipe();
        recipe.setId(1L);

        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient());
        ingredients.get(0).setId(1L);
        ingredients.get(0).setName("Salt");
        recipe.setIngredients(ingredients);

        Review review = new Review();
        review.setId(1L);
        review.setText("Delicious!");
        review.setRating(5);
        review.setRecipe(recipe);
        List<Review> reviews = new ArrayList<>();
        reviews.add(review);
        recipe.setReviews(reviews);

        when(recipeDao.createRecipe(any(Recipe.class))).thenReturn(recipe);
        doNothing().when(cacheService).updateCache(any(), any());

        RecipeDto createdRecipe = recipeService.createRecipe(recipeDto);

        assertNotNull(createdRecipe);
        assertEquals(recipe.getId(), createdRecipe.getId());
        assertEquals(1, createdRecipe.getIngredients().size());
        assertEquals(1, createdRecipe.getReviews().size());
    }

    @Test
    public void testUpdateRecipe_WithIngredientsAndReviews() {
        RecipeDto recipeDto = new RecipeDto(1L, "Updated Recipe", "Updated Description",
                List.of(new IngredientDto(1L, "Salt")),
                List.of(new ReviewDto(1L, "Even better!", 4, 1L)), null);

        Recipe recipe = new Recipe();
        recipe.setId(1L);

        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient());
        ingredients.get(0).setId(1L);
        ingredients.get(0).setName("Salt");
        recipe.setIngredients(ingredients);

        Review review = new Review();
        review.setId(1L);
        review.setText("Even better!");
        review.setRating(4);
        review.setRecipe(recipe);
        List<Review> reviews = new ArrayList<>();
        reviews.add(review);
        recipe.setReviews(reviews);

        when(recipeDao.getRecipeById(1L)).thenReturn(recipe);
        when(recipeDao.updateRecipe(eq(1L), any())).thenReturn(recipe);

        RecipeDto updatedRecipe = recipeService.updateRecipe(1L, recipeDto);

        assertNotNull(updatedRecipe);
        assertEquals(recipe.getId(), updatedRecipe.getId());
        assertEquals(1, updatedRecipe.getIngredients().size());
        assertEquals(1, updatedRecipe.getReviews().size());
    }

    @Test
    public void testCreateRecipe_WithoutIngredientsAndReviews() {
        RecipeDto recipeDto = new RecipeDto(1L, "Test Recipe", "Description", null, null, null);
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        when(recipeDao.createRecipe(any())).thenReturn(recipe);
        doNothing().when(cacheService).updateCache(any(), any());

        RecipeDto createdRecipe = recipeService.createRecipe(recipeDto);

        assertNotNull(createdRecipe);
        assertEquals(recipe.getId(), createdRecipe.getId());
        assertTrue(createdRecipe.getIngredients().isEmpty());
        assertTrue(createdRecipe.getReviews().isEmpty());
    }

    @Test
    public void testGetRecipesByCuisineName_CachedAndDatabase() {
        String cuisineName = "Italian";
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Pasta");
        recipe.setDescription("Delicious pasta recipe");
        List<Recipe> recipes = List.of(recipe);

        when(cacheService.getCachedRecipes(cuisineName)).thenReturn(null);
        when(recipeDao.findRecipesByCuisineNameJpql(cuisineName)).thenReturn(recipes);

        List<RecipeDto> result = recipeService.getRecipesByCuisineName(cuisineName);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(recipe.getId(), result.get(0).getId());
        verify(cacheService, times(1)).cacheRecipes(cuisineName, result);
    }

    @Test
    public void testGetRecipesByCuisineName_NoResultsInDatabase() {
        String cuisineName = "Italian";

        when(cacheService.getCachedRecipes(cuisineName)).thenReturn(null);
        when(recipeDao.findRecipesByCuisineNameJpql(cuisineName)).thenReturn(new ArrayList<>());
        when(recipeDao.findRecipesByCuisineNameNative(cuisineName)).thenReturn(new ArrayList<>());

        List<RecipeDto> result = recipeService.getRecipesByCuisineName(cuisineName);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeDao, times(1)).findRecipesByCuisineNameJpql(cuisineName);
        verify(recipeDao, times(1)).findRecipesByCuisineNameNative(cuisineName);
        verify(cacheService, never()).cacheRecipes(any(), any());
    }

    @Test
    public void testCreateRecipe_EmptyTitle() {
        RecipeDto recipeDto = new RecipeDto(1L, null, "Description", new ArrayList<>(), new ArrayList<>(), null);

        Exception exception = assertThrows(CustomException.class, () -> {
            recipeService.createRecipe(recipeDto);
        });

        assertEquals("Название и описание не могут быть пустыми", exception.getMessage());
    }

    @Test
    public void testCreateRecipe_EmptyDescription() {
        RecipeDto recipeDto = new RecipeDto(1L, "Test Recipe", null, new ArrayList<>(), new ArrayList<>(), null);

        Exception exception = assertThrows(CustomException.class, () -> {
            recipeService.createRecipe(recipeDto);
        });

        assertEquals("Название и описание не могут быть пустыми", exception.getMessage());
    }

    @Test
    public void testUpdateRecipe_NullInput() {
        RecipeDto recipeDto = null;

        RecipeDto updatedRecipe = recipeService.updateRecipe(1L, recipeDto);
        assertNull(updatedRecipe);
        verify(recipeDao, never()).updateRecipe(any(), any());
    }

    @Test
    public void testUpdateRecipe_CuisineDtoIsNull() {
        Long recipeId = 1L;
        RecipeDto recipeDto = new RecipeDto(recipeId, "Updated Title", "Updated Description", new ArrayList<>(), new ArrayList<>(), null);

        Recipe existingRecipe = new Recipe();
        existingRecipe.setId(recipeId);
        existingRecipe.setTitle("Original Title");
        existingRecipe.setDescription("Original Description");
        Cuisine cuisine = new Cuisine();
        cuisine.setId(1L);
        existingRecipe.setCuisine(cuisine);

        Recipe updatedRecipe = new Recipe();
        updatedRecipe.setId(recipeId);
        updatedRecipe.setTitle("Updated Title");
        updatedRecipe.setDescription("Updated Description");
        updatedRecipe.setCuisine(null);

        when(recipeDao.getRecipeById(recipeId)).thenReturn(existingRecipe);
        when(recipeDao.updateRecipe(eq(recipeId), any(Recipe.class))).thenReturn(updatedRecipe);

        RecipeDto updatedRecipeDto = recipeService.updateRecipe(recipeId, recipeDto);

        assertNotNull(updatedRecipeDto);
        assertEquals(recipeId, updatedRecipeDto.getId());
        assertEquals("Updated Title", updatedRecipeDto.getTitle());
        assertEquals("Updated Description", updatedRecipeDto.getDescription());
        assertNull(updatedRecipeDto.getCuisine());

        verify(recipeDao, times(1)).getRecipeById(recipeId);
        verify(recipeDao, times(1)).updateRecipe(eq(recipeId), any(Recipe.class));
    }

    @Test
    public void testUpdateRecipe_WithNullCuisineDto_ExistingCuisineNotNull() {
        Long recipeId = 1L;
        RecipeDto recipeDto = new RecipeDto(recipeId, "Updated Title", "Updated Description", new ArrayList<>(), new ArrayList<>(), null);

        Recipe existingRecipe = new Recipe();
        existingRecipe.setId(recipeId);
        existingRecipe.setTitle("Original Title");
        existingRecipe.setDescription("Original Description");
        Cuisine cuisine = new Cuisine();
        cuisine.setId(1L);
        cuisine.setName("Existing Cuisine");
        existingRecipe.setCuisine(cuisine);

        Recipe updatedRecipe = new Recipe();
        updatedRecipe.setId(recipeId);
        updatedRecipe.setTitle("Updated Title");
        updatedRecipe.setDescription("Updated Description");

        when(recipeDao.getRecipeById(recipeId)).thenReturn(existingRecipe);
        when(recipeDao.updateRecipe(eq(recipeId), any(Recipe.class))).thenReturn(updatedRecipe);

        RecipeDto updatedRecipeDto = recipeService.updateRecipe(recipeId, recipeDto);

        assertNotNull(updatedRecipeDto);
        assertEquals(recipeId, updatedRecipeDto.getId());
        assertEquals("Updated Title", updatedRecipeDto.getTitle());
        assertEquals("Updated Description", updatedRecipeDto.getDescription());
        assertNull(updatedRecipeDto.getCuisine());

        verify(recipeDao, times(1)).getRecipeById(recipeId);
        verify(recipeDao, times(1)).updateRecipe(eq(recipeId), any(Recipe.class));
    }

    @Test
    public void testGetAllRecipes_EmptyList() {
        when(recipeDao.getAllRecipes()).thenReturn(new ArrayList<>());

        List<RecipeDto> recipeDtos = recipeService.getAllRecipes();

        assertNotNull(recipeDtos);
        assertTrue(recipeDtos.isEmpty());
    }

    @Test
    public void testCreateRecipe_WithNullCuisineAndNullIngredients() {
        RecipeDto recipeDto = new RecipeDto(1L, "Test Recipe", "Description", null, null, null);
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        when(recipeDao.createRecipe(any())).thenReturn(recipe);
        doNothing().when(cacheService).updateCache(any(), any());

        RecipeDto createdRecipe = recipeService.createRecipe(recipeDto);

        assertNotNull(createdRecipe);
        assertEquals(recipe.getId(), createdRecipe.getId());
        verify(recipeDao, times(1)).createRecipe(any());
        assertTrue(createdRecipe.getIngredients().isEmpty());
        assertNull(createdRecipe.getCuisine());
    }

    @Test
    public void testGetAllRecipes_WithNullIngredientsReviewsAndCuisine() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Test Recipe");
        recipe.setDescription("Description");
        recipe.setIngredients(null);
        recipe.setReviews(null);
        recipe.setCuisine(null);

        List<Recipe> recipes = List.of(recipe);
        when(recipeDao.getAllRecipes()).thenReturn(recipes);

        List<RecipeDto> recipeDtos = recipeService.getAllRecipes();

        assertNotNull(recipeDtos);
        assertEquals(1, recipeDtos.size());

        RecipeDto recipeDto = recipeDtos.get(0);
        assertNotNull(recipeDto);
        assertEquals(1L, recipeDto.getId());
        assertEquals("Test Recipe", recipeDto.getTitle());
        assertEquals("Description", recipeDto.getDescription());
        assertNotNull(recipeDto.getIngredients());
        assertTrue(recipeDto.getIngredients().isEmpty());
        assertNotNull(recipeDto.getReviews());
        assertTrue(recipeDto.getReviews().isEmpty());
        assertNull(recipeDto.getCuisine());
    }

    @Test
    public void testGetRecipeById_WithNullIngredientsReviewsAndCuisine() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Test Recipe");
        recipe.setDescription("Description");
        recipe.setIngredients(null);
        recipe.setReviews(null);
        recipe.setCuisine(null);

        when(recipeDao.getRecipeById(1L)).thenReturn(recipe);

        RecipeDto recipeDto = recipeService.getRecipeById(1L);

        assertNotNull(recipeDto);
        assertEquals(1L, recipeDto.getId());
        assertEquals("Test Recipe", recipeDto.getTitle());
        assertEquals("Description", recipeDto.getDescription());
        assertNotNull(recipeDto.getIngredients());
        assertTrue(recipeDto.getIngredients().isEmpty());
        assertNotNull(recipeDto.getReviews());
        assertTrue(recipeDto.getReviews().isEmpty());
        assertNull(recipeDto.getCuisine());
    }
}