package recipeservice.sevice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import recipeservice.dao.ReviewDao;
import recipeservice.dto.ReviewDto;
import recipeservice.model.Review;
import recipeservice.model.Recipe;
import recipeservice.service.ReviewService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ReviewServiceTest {

    @Mock
    private ReviewDao reviewDao;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllReviews() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        Review review1 = new Review();
        review1.setId(1L);
        review1.setText("Great recipe!");
        review1.setRating(5);
        review1.setRecipe(recipe);

        Review review2 = new Review();
        review2.setId(2L);
        review2.setText("Not bad.");
        review2.setRating(3);
        review2.setRecipe(recipe);

        when(reviewDao.getAllReviews()).thenReturn(Arrays.asList(review1, review2));

        List<ReviewDto> reviews = reviewService.getAllReviews();

        assertEquals(2, reviews.size());
        assertEquals("Great recipe!", reviews.get(0).getText());
        assertEquals(5, reviews.get(0).getRating());
        assertEquals(1L, reviews.get(0).getRecipeId());
        verify(reviewDao, times(1)).getAllReviews();
    }

    @Test
    public void testGetReviewById() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        Review review = new Review();
        review.setId(1L);
        review.setText("Great recipe!");
        review.setRating(5);
        review.setRecipe(recipe);

        when(reviewDao.getReviewById(1L)).thenReturn(review);

        ReviewDto foundReview = reviewService.getReviewById(1L);

        assertNotNull(foundReview);
        assertEquals("Great recipe!", foundReview.getText());
        assertEquals(5, foundReview.getRating());
        assertEquals(1L, foundReview.getRecipeId());
        verify(reviewDao, times(1)).getReviewById(1L);
    }

    @Test
    public void testCreateReview() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        Review review = new Review();
        review.setId(1L);
        review.setText("Great recipe!");
        review.setRating(5);
        review.setRecipe(recipe);

        when(reviewDao.createReview(any(Review.class))).thenReturn(review);

        Review createdReview = reviewService.createReview(review);

        assertNotNull(createdReview);
        assertEquals("Great recipe!", createdReview.getText());
        assertEquals(5, createdReview.getRating());
        assertEquals(1L, createdReview.getRecipe().getId());
        verify(reviewDao, times(1)).createReview(any(Review.class));
    }

    @Test
    public void testUpdateReview() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        Review review = new Review();
        review.setId(1L);
        review.setText("Great recipe!");
        review.setRating(5);
        review.setRecipe(recipe);

        when(reviewDao.updateReview(eq(1L), any(Review.class))).thenReturn(review);

        Review updatedReview = reviewService.updateReview(1L, review);

        assertNotNull(updatedReview);
        assertEquals("Great recipe!", updatedReview.getText());
        assertEquals(5, updatedReview.getRating());
        assertEquals(1L, updatedReview.getRecipe().getId());
        verify(reviewDao, times(1)).updateReview(eq(1L), any(Review.class));
    }

    @Test
    public void testDeleteReview() {
        reviewService.deleteReview(1L);

        verify(reviewDao, times(1)).deleteReview(1L);
    }

    @Test
    public void testGetReviewById_NotFound() {
        when(reviewDao.getReviewById(1L)).thenReturn(null);

        ReviewDto foundReview = reviewService.getReviewById(1L);

        assertNull(foundReview);
        verify(reviewDao, times(1)).getReviewById(1L);
    }

    @Test
    public void testUpdateReview_WithEmptyValues() {
        Review review = new Review();
        review.setId(1L);
        review.setText(null);
        review.setRating(null);
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        review.setRecipe(recipe);

        when(reviewDao.updateReview(eq(1L), any(Review.class))).thenReturn(review);

        Review updatedReview = reviewService.updateReview(1L, review);

        assertNotNull(updatedReview);
        assertNull(updatedReview.getText());
        assertNull(updatedReview.getRating());
        assertEquals(1L, updatedReview.getRecipe().getId());
        verify(reviewDao, times(1)).updateReview(eq(1L), any(Review.class));
    }
}