package recipeservice.sevice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import recipeservice.dao.IngredientDao;
import recipeservice.dao.RecipeDao;
import recipeservice.dto.IngredientDto;
import recipeservice.model.Ingredient;
import recipeservice.model.Recipe;
import recipeservice.service.IngredientService;
import recipeservice.exception.CustomException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class IngredientServiceTest {

    @Mock
    private IngredientDao ingredientDao;

    @Mock
    private RecipeDao recipeDao;

    @InjectMocks
    private IngredientService ingredientService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllIngredients() {
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId(1L);
        ingredient1.setName("Salt");
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId(2L);
        ingredient2.setName("Pepper");

        when(ingredientDao.getAllIngredients()).thenReturn(Arrays.asList(ingredient1, ingredient2));

        List<IngredientDto> ingredients = ingredientService.getAllIngredients();

        assertEquals(2, ingredients.size());
        assertEquals("Salt", ingredients.get(0).getName());
        assertEquals("Pepper", ingredients.get(1).getName());
        assertEquals(1L, ingredients.get(0).getId());
        assertEquals(2L, ingredients.get(1).getId());
        verify(ingredientDao, times(1)).getAllIngredients();
    }

    @Test
    public void testGetIngredientById() {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Salt");
        when(ingredientDao.getIngredientById(1L)).thenReturn(ingredient);

        IngredientDto foundIngredient = ingredientService.getIngredientById(1L);

        assertNotNull(foundIngredient);
        assertEquals("Salt", foundIngredient.getName());
        assertEquals(1L, foundIngredient.getId());
        verify(ingredientDao, times(1)).getIngredientById(1L);
    }

    @Test
    public void testCreateIngredient() {
        IngredientDto ingredientDto = new IngredientDto(null, "Salt");
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Salt");

        when(ingredientDao.createIngredient(any(Ingredient.class))).thenReturn(ingredient);

        IngredientDto createdIngredient = ingredientService.createIngredient(ingredientDto);

        assertNotNull(createdIngredient);
        assertEquals("Salt", createdIngredient.getName());
        verify(ingredientDao, times(1)).createIngredient(any(Ingredient.class));
    }

    @Test
    public void testUpdateIngredient() {
        IngredientDto ingredientDto = new IngredientDto(1L, "Salt");
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Salt");

        when(ingredientDao.updateIngredient(eq(1L), any(Ingredient.class))).thenReturn(ingredient);

        IngredientDto updatedIngredient = ingredientService.updateIngredient(1L, ingredientDto);

        assertNotNull(updatedIngredient);
        assertEquals("Salt", updatedIngredient.getName());
        verify(ingredientDao, times(1)).updateIngredient(eq(1L), any(Ingredient.class));
    }

    @Test
    public void testDeleteIngredient() {
        Recipe recipe = new Recipe();
        recipe.getIngredients().add(new Ingredient() {{ setId(1L); setName("Salt"); }});
        when(recipeDao.findRecipesByIngredientId(1L)).thenReturn(Arrays.asList(recipe));

        ingredientService.deleteIngredient(1L);

        verify(recipeDao, times(1)).findRecipesByIngredientId(1L);
        verify(ingredientDao, times(1)).deleteIngredient(1L);
    }

    @Test
    public void testGetIngredientById_NotFound() {
        when(ingredientDao.getIngredientById(1L)).thenReturn(null);

        IngredientDto foundIngredient = ingredientService.getIngredientById(1L);

        assertNull(foundIngredient);
        verify(ingredientDao, times(1)).getIngredientById(1L);
    }

    @Test
    public void testCreateIngredient_WithEmptyName() {
        IngredientDto ingredientDto = new IngredientDto(null, null); // Пустое имя
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName(null); // Пустое имя

        when(ingredientDao.createIngredient(any(Ingredient.class))).thenReturn(ingredient);

        IngredientDto createdIngredient = ingredientService.createIngredient(ingredientDto);

        assertNotNull(createdIngredient);
        assertNull(createdIngredient.getName());
        verify(ingredientDao, times(1)).createIngredient(any(Ingredient.class));
    }

    @Test
    public void testUpdateIngredient_WithEmptyName() {
        IngredientDto ingredientDto = new IngredientDto(1L, null);
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName(null);

        when(ingredientDao.updateIngredient(eq(1L), any(Ingredient.class))).thenReturn(ingredient);

        IngredientDto updatedIngredient = ingredientService.updateIngredient(1L, ingredientDto);

        assertNotNull(updatedIngredient);
        assertNull(updatedIngredient.getName());
        verify(ingredientDao, times(1)).updateIngredient(eq(1L), any(Ingredient.class));
    }

    @Test
    public void testCreateIngredient_NullInput() {
        IngredientDto createdIngredient = ingredientService.createIngredient(null);

        assertNull(createdIngredient);
        verify(ingredientDao, never()).createIngredient(any(Ingredient.class));
    }

    @Test
    public void testUpdateIngredient_NullInput() {
        IngredientDto updatedIngredient = ingredientService.updateIngredient(1L, null);

        assertNull(updatedIngredient);
        verify(ingredientDao, never()).updateIngredient(eq(1L), any(Ingredient.class));
    }

    @Test
    public void testUpdateIngredient_NotFound() {
        IngredientDto ingredientDto = new IngredientDto(1L, "Salt");
        when(ingredientDao.updateIngredient(eq(1L), any(Ingredient.class))).thenReturn(null); // Ингредиент не найден

        IngredientDto updatedIngredient = ingredientService.updateIngredient(1L, ingredientDto);

        assertNull(updatedIngredient);
        verify(ingredientDao, times(1)).updateIngredient(eq(1L), any(Ingredient.class));
    }

    @Test
    public void testDeleteIngredient_NotFound() {
        when(recipeDao.findRecipesByIngredientId(1L)).thenReturn(Arrays.asList());

        ingredientService.deleteIngredient(1L);

        verify(recipeDao, times(1)).findRecipesByIngredientId(1L);
        verify(ingredientDao, times(1)).deleteIngredient(1L);
    }
}