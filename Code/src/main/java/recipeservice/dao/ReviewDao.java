package recipeservice.dao;

import java.util.List;
import recipeservice.model.Review;

public interface ReviewDao {
    List<Review> getAllReviews();

    Review getReviewById(Long id);

    Review createReview(Review review);

    Review updateReview(Long id, Review review);

    void deleteReview(Long id);

    List<Review> getReviewsByRecipeId(Long recipeId);
}