package recipeservice.sevice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import recipeservice.dao.CuisineDao;
import recipeservice.dto.CuisineDto;
import recipeservice.model.Cuisine;
import recipeservice.service.CuisineService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CuisineServiceTest {

    @Mock
    private CuisineDao cuisineDao;

    @InjectMocks
    private CuisineService cuisineService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllCuisines() {
        Cuisine cuisine1 = new Cuisine(1L, "Italian");
        Cuisine cuisine2 = new Cuisine(2L, "Mexican");
        when(cuisineDao.getAllCuisines()).thenReturn(Arrays.asList(cuisine1, cuisine2));

        List<CuisineDto> cuisines = cuisineService.getAllCuisines();

        assertEquals(2, cuisines.size());
        assertEquals("Italian", cuisines.get(0).getName());
        assertEquals("Mexican", cuisines.get(1).getName());
        verify(cuisineDao, times(1)).getAllCuisines();
    }

    @Test
    public void testGetCuisineById() {
        Cuisine cuisine = new Cuisine(1L, "Italian");
        when(cuisineDao.getCuisineById(1L)).thenReturn(cuisine);

        CuisineDto foundCuisine = cuisineService.getCuisineById(1L);

        assertNotNull(foundCuisine);
        assertEquals("Italian", foundCuisine.getName());
        verify(cuisineDao, times(1)).getCuisineById(1L);
    }

    @Test
    public void testCreateCuisine() {
        CuisineDto cuisineDto = new CuisineDto(null, "Italian");
        Cuisine cuisine = new Cuisine(null, "Italian");
        when(cuisineDao.createCuisine(any(Cuisine.class))).thenReturn(cuisine);

        CuisineDto createdCuisine = cuisineService.createCuisine(cuisineDto);

        assertNotNull(createdCuisine);
        assertEquals("Italian", createdCuisine.getName());
        verify(cuisineDao, times(1)).createCuisine(any(Cuisine.class));
    }

    @Test
    public void testUpdateCuisine() {
        CuisineDto cuisineDto = new CuisineDto(1L, "French");
        Cuisine updatedCuisine = new Cuisine(1L, "French");
        when(cuisineDao.updateCuisine(eq(1L), any(Cuisine.class))).thenReturn(updatedCuisine);

        CuisineDto result = cuisineService.updateCuisine(1L, cuisineDto);

        assertNotNull(result);
        assertEquals("French", result.getName());
        verify(cuisineDao, times(1)).updateCuisine(eq(1L), any(Cuisine.class));
    }

    @Test
    public void testDeleteCuisine() {
        cuisineService.deleteCuisine(1L);

        verify(cuisineDao, times(1)).deleteCuisine(1L);
    }
}