package recipeservice.dto;

import jakarta.validation.constraints.NotBlank;

public class CuisineDto {
    @NotBlank(message = "Айди не может быть пустым")
    private final Long id;
    @NotBlank(message = "Имя не может быть пустым")
    private final String name;

    public CuisineDto(Long id, String name) {
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