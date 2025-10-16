package recipeservice.service;

import java.util.List;
import org.springframework.stereotype.Service;
import recipeservice.dao.ReviewDao;
import recipeservice.dto.ReviewDto;
import recipeservice.model.Review;
import recipeservice.exception.CustomException;

@Service
public class ReviewService {

    private final ReviewDao reviewDao;

    public ReviewService(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    public List<ReviewDto> getAllReviews() {
        List<Review> reviews = reviewDao.getAllReviews();
        return reviews.stream()
                .map(review -> new ReviewDto(
                        review.getId(),
                        review.getText(),
                        review.getRating(),
                        review.getRecipe().getId()))
                .toList();
    }

    public ReviewDto getReviewById(Long id) {
        Review review = reviewDao.getReviewById(id);
        return review != null
                ? new ReviewDto(
                        review.getId(),
                        review.getText(),
                        review.getRating(),
                        review.getRecipe().getId())
                : null;
    }

    public Review createReview(Review review) {
        return reviewDao.createReview(review);
    }

    public Review updateReview(Long id, Review review) {
        Review existingReview = reviewDao.updateReview(id, review);
        if (existingReview == null) {
            throw new CustomException("Отзыв не найден для обновления");
        }
        return existingReview;
    }

    public void deleteReview(Long id) {
        reviewDao.deleteReview(id);
    }

    public List<ReviewDto> getReviewsByRecipeId(Long recipeId) {
        List<Review> reviews = reviewDao.getReviewsByRecipeId(recipeId);
        return reviews.stream()
                .map(review -> new ReviewDto(
                        review.getId(),
                        review.getText(),
                        review.getRating(),
                        review.getRecipe().getId()))
                .toList();
    }
}


// /Library/PostgreSQL/17/bin/postgres -D /Library/PostgreSQL/17/data