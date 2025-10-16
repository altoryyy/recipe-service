package recipeservice.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;
import recipeservice.dao.CuisineDao;
import recipeservice.model.Cuisine;

@Repository
@Transactional
public class CuisineDaoImpl implements CuisineDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Cuisine> getAllCuisines() {
        return entityManager.createQuery("FROM Cuisine", Cuisine.class).getResultList();
    }

    @Override
    public Cuisine getCuisineById(Long id) {
        return entityManager.find(Cuisine.class, id);
    }

    @Override
    public Cuisine createCuisine(Cuisine cuisine) {
        entityManager.persist(cuisine);
        return cuisine;
    }

    @Override
    public Cuisine updateCuisine(Long id, Cuisine cuisine) {
        cuisine.setId(id);
        return entityManager.merge(cuisine);
    }

    @Override
    public void deleteCuisine(Long id) {
        Cuisine cuisine = getCuisineById(id);
        if (cuisine != null) {
            entityManager.remove(cuisine);
        }
    }
}