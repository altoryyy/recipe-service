package recipeservice.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;
import recipeservice.dao.IngredientDao;
import recipeservice.model.Ingredient;

@Repository
@Transactional
public class IngredientDaoImpl implements IngredientDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Ingredient> getAllIngredients() {
        return entityManager.createQuery("FROM Ingredient", Ingredient.class).getResultList();
    }

    @Override
    public Ingredient getIngredientById(Long id) {
        return entityManager.find(Ingredient.class, id);
    }

    @Override
    public Ingredient createIngredient(Ingredient ingredient) {
        entityManager.persist(ingredient);
        return ingredient;
    }

    @Override
    public Ingredient updateIngredient(Long id, Ingredient ingredient) {
        ingredient.setId(id);
        return entityManager.merge(ingredient);
    }

    @Override
    public void deleteIngredient(Long id) {
        Ingredient ingredient = getIngredientById(id);
        if (ingredient != null) {
            entityManager.remove(ingredient);
        }
    }
}