package recipeservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Integer getRating() {
        return rating;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}