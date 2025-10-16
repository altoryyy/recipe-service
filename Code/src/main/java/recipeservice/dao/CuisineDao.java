package recipeservice.dao;

import java.util.List;
import recipeservice.model.Cuisine;

public interface CuisineDao {
    List<Cuisine> getAllCuisines();

    Cuisine getCuisineById(Long id);

    Cuisine createCuisine(Cuisine cuisine);

    Cuisine updateCuisine(Long id, Cuisine cuisine);

    void deleteCuisine(Long id);
}