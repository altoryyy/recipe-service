package recipeservice.dto;

import jakarta.validation.constraints.NotBlank;

public class IngredientDto {
    @NotBlank(message = "Айди не может быть пустым")
    private final Long id;
    @NotBlank(message = "Имя не может быть пустым")
    private final String name;

    public IngredientDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}