package recipeservice.sevice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import recipeservice.dto.RecipeDto;
import recipeservice.service.CacheService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CacheServiceTest {

    private CacheService cacheService;

    @BeforeEach
    public void setUp() {
        cacheService = new CacheService();
    }

    @Test
    public void testCacheRecipes() {
        String key = "Italian";
        List<RecipeDto> recipes = new ArrayList<>();
        recipes.add(new RecipeDto(1L, "Pasta", "Delicious pasta recipe", new ArrayList<>(), new ArrayList<>(), null));

        cacheService.cacheRecipes(key, recipes);

        List<RecipeDto> cachedRecipes = cacheService.getCachedRecipes(key);
        assertNotNull(cachedRecipes);
        assertEquals(1, cachedRecipes.size());
        assertEquals("Pasta", cachedRecipes.get(0).getTitle());
    }

    @Test
    public void testRemoveCachedRecipes() {
        String key = "Italian";
        List<RecipeDto> recipes = new ArrayList<>();
        recipes.add(new RecipeDto(1L, "Pasta", "Delicious pasta recipe", new ArrayList<>(), new ArrayList<>(), null));

        cacheService.cacheRecipes(key, recipes);
        cacheService.removeCachedRecipes(key);

        List<RecipeDto> cachedRecipes = cacheService.getCachedRecipes(key);
        assertNull(cachedRecipes);
    }

    @Test
    public void testUpdateCache_NewEntry() {
        String key = "Italian";
        RecipeDto newRecipe = new RecipeDto(1L, "Pasta", "Delicious pasta recipe", new ArrayList<>(), new ArrayList<>(), null);

        cacheService.updateCache(key, newRecipe);
        List<RecipeDto> cachedRecipes = cacheService.getCachedRecipes(key);

        assertNotNull(cachedRecipes);
    }

    @Test
    public void testUpdateCache_ExistingEntry() {
        String key = "Italian";
        RecipeDto existingRecipe = new RecipeDto(1L, "Pasta", "Delicious pasta recipe", new ArrayList<>(), new ArrayList<>(), null);
        RecipeDto newRecipe = new RecipeDto(2L, "Lasagna", "Tasty lasagna recipe", new ArrayList<>(), new ArrayList<>(), null);

        cacheService.updateCache(key, existingRecipe);
        cacheService.updateCache(key, newRecipe);

        List<RecipeDto> cachedRecipes = cacheService.getCachedRecipes(key);
        assertNotNull(cachedRecipes);
        assertEquals(2, cachedRecipes.size());
        assertEquals("Pasta", cachedRecipes.get(0).getTitle());
        assertEquals("Lasagna", cachedRecipes.get(1).getTitle());
    }

    @Test
    public void testGetCachedRecipes_NullKey() {
        List<RecipeDto> cachedRecipes = cacheService.getCachedRecipes(null);
        assertNull(cachedRecipes);
    }

    @Test
    public void testCacheRecipes_FullCache() {
        for (int i = 0; i < 100; i++) {
            cacheService.cacheRecipes("key" + i, new ArrayList<>());
        }

        cacheService.cacheRecipes("key100", new ArrayList<>());

        assertNull(cacheService.getCachedRecipes("key0"));
        assertNotNull(cacheService.getCachedRecipes("key1"));
        assertNotNull(cacheService.getCachedRecipes("key100"));
    }

    @Test
    public void testRemoveCachedRecipes_NotFound() {
        String key = "NonExistentKey";

        cacheService.removeCachedRecipes(key);
        assertNull(cacheService.getCachedRecipes(key));
    }

    @Test
    public void testGetCachedRecipes_ExpiredEntry() throws InterruptedException {
        String key = "Italian";
        List<RecipeDto> recipes = new ArrayList<>();
        recipes.add(new RecipeDto(1L, "Pasta", "Delicious pasta recipe", new ArrayList<>(), new ArrayList<>(), null));

        cacheService.cacheRecipes(key, recipes);
        Thread.sleep(2000);

        List<RecipeDto> cachedRecipes = cacheService.getCachedRecipes(key);
        assertNull(cachedRecipes);
    }

    @Test
    public void testCacheRecipes_EmptyList() {
        cacheService.cacheRecipes("keyEmpty", new ArrayList<>());
        List<RecipeDto> cachedRecipes = cacheService.getCachedRecipes("keyEmpty");

        assertNotNull(cachedRecipes);
        assertTrue(cachedRecipes.isEmpty());
    }
}