package recipeservice.dto;

public class ReviewDto {
    private final Long id;
    private final String text;
    private final Integer rating;
    private final Long recipeId;

    public ReviewDto(Long id, String text, Integer rating, Long recipeId) {
        this.id = id;
        this.text = text;
        this.rating = rating;
        this.recipeId = recipeId;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Integer getRating() {
        return rating;
    }

    public Long getRecipeId() {
        return recipeId;
    }
}