package recipeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import recipeservice.dto.RecipeDto;
import recipeservice.dto.ReviewDto;
import recipeservice.exception.CustomException;
import recipeservice.log.VisitCounter;
import recipeservice.model.Recipe;
import recipeservice.model.Review;
import recipeservice.service.RecipeService;
import recipeservice.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final RecipeService recipeService;
    private final VisitCounter visitCounter;

    @Autowired
    public ReviewController(ReviewService reviewService,
                            RecipeService recipeService,
                            VisitCounter visitCounter) {
        this.reviewService = reviewService;
        this.recipeService = recipeService;
        this.visitCounter = visitCounter;
    }

    @Operation(summary = "Получить все отзывы",
        description = "Возвращает список всех отзывов в системе.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно получены все отзывы"),
    })
    @GetMapping
    public List<ReviewDto> getAllReviews() {
        visitCounter.incrementVisit("/api/reviews");
        return reviewService.getAllReviews();
    }

    @Operation(summary = "Получить отзыв по ID",
        description = "Возвращает отзыв с указанным ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Отзыв найден"),
        @ApiResponse(responseCode = "404", description = "Отзыв не найден")
    })
    @GetMapping("/{id}")
    public ReviewDto getReviewById(@PathVariable Long id) {
        visitCounter.incrementVisit("/api/reviews/" + id);
        return reviewService.getReviewById(id);
    }

    @Operation(summary = "Создать новый отзыв",
        description = "Создает новый отзыв с заданными параметрами.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Отзыв успешно создан"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных"),
        @ApiResponse(responseCode = "404", description = "Рецепт не найден")
    })
    @PostMapping
    public Review createReview(@RequestBody Review review) {
        visitCounter.incrementVisit("/api/reviews");
        if (review == null || review.getText() == null || review.getRecipe() == null) {
            throw new CustomException("Содержимое отзыва и рецепт не могут быть пустыми");
        }

        RecipeDto recipeDto = recipeService.getRecipeById(review.getRecipe().getId());
        if (recipeDto == null) {
            throw new CustomException("Рецепт не найден");
        }

        review.setRecipe(convertToEntity(recipeDto));
        return reviewService.createReview(review);
    }

    private Recipe convertToEntity(RecipeDto recipeDto) {
        Recipe recipe = new Recipe();
        recipe.setId(recipeDto.getId());
        return recipe;
    }

    @Operation(summary = "Обновить отзыв по ID",
            description = "Обновляет существующий отзыв с указанным ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Отзыв успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Отзыв не найден"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    })
    @PutMapping("/{id}")
    public Review updateReview(@PathVariable Long id, @RequestBody Review review) {
        visitCounter.incrementVisit("/api/reviews/" + id);

        if (review == null || review.getText() == null) {
            throw new CustomException("Содержимое отзыва не может быть пустым");
        }

        return reviewService.updateReview(id, review);
    }

    @Operation(summary = "Удалить отзыв по ID",
            description = "Удаляет отзыв с указанным ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Отзыв успешно удален"),
        @ApiResponse(responseCode = "404", description = "Отзыв не найден")
    })
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    @Operation(summary = "Получить отзывы по ID рецепта",
            description = "Возвращает список всех отзывов для указанного рецепта.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Отзывы найдены"),
        @ApiResponse(responseCode = "404", description = "Рецепт не найден")
    })
    @GetMapping("/recipe/{recipeId}")
    public List<ReviewDto> getReviewsByRecipeId(@PathVariable Long recipeId) {
        return reviewService.getReviewsByRecipeId(recipeId);
    }
}

