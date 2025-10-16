package recipeservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class RecipeDto {
    @NotBlank(message = "Айди не может быть пустым")
    private final Long id;
    @NotBlank(message = "Заголовок не может быть пустым")
    private final String title;
    @NotBlank(message = "Описание не может быть пустым")
    private final String description;
    @NotBlank(message = "Рецепты не могут быть пустыми")
    private final List<IngredientDto> ingredients;
    @NotBlank(message = "Отзывы не могут быть пыстыми")
    private final List<ReviewDto> reviews;
    @NotNull(message = "Кухня не может быть пустой")
    private final CuisineDto cuisine;

    public RecipeDto(
            Long id, String title,
            String description,
            List<IngredientDto> ingredients,
            List<ReviewDto> reviews,
            CuisineDto cuisine) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.reviews = reviews;
        this.cuisine = cuisine;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<IngredientDto> getIngredients() {
        return ingredients;
    }

    public List<ReviewDto> getReviews() {
        return reviews;
    }

    public CuisineDto getCuisine() {
        return cuisine;
    }
}