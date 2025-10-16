package recipeservice.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;
import recipeservice.dao.ReviewDao;
import recipeservice.model.Review;

@Repository
public class ReviewDaoImpl implements ReviewDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Review> getAllReviews() {
        return entityManager.createQuery("SELECT r FROM Review r", Review.class).getResultList();
    }

    @Override
    public Review getReviewById(Long id) {
        return entityManager.find(Review.class, id);
    }

    @Override
    @Transactional
    public Review createReview(Review review) {
        entityManager.persist(review);
        return review;
    }

    @Override
    @Transactional
    public Review updateReview(Long id, Review review) {
        Review existingReview = entityManager.find(Review.class, id);
        if (existingReview != null) {
            existingReview.setText(review.getText());
            existingReview.setRating(review.getRating());
            return existingReview;
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = entityManager.find(Review.class, id);
        if (review != null) {
            entityManager.remove(review);
        }
    }

    @Override
    public List<Review> getReviewsByRecipeId(Long recipeId) {
        return entityManager.createQuery(
                        "SELECT r FROM Review r WHERE r.recipe.id = :recipeId",
                        Review.class)
                .setParameter("recipeId", recipeId)
                .getResultList();
    }
}