package recipeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import recipeservice.dto.RecipeDto;
import recipeservice.exception.CustomException;
import recipeservice.log.VisitCounter;
import recipeservice.service.RecipeService;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;
    private final VisitCounter visitCounter;

    public RecipeController(RecipeService recipeService, VisitCounter visitCounter) {
        this.recipeService = recipeService;
        this.visitCounter = visitCounter;
    }

    @Operation(summary = "Получить все рецепты",
            description = "Возвращает список всех рецептов в системе.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно получены все рецепты"),
        @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping
    public List<RecipeDto> getAllRecipes() {
        visitCounter.incrementVisit("/api/recipes");
        return recipeService.getAllRecipes();
    }

    @Operation(summary = "Получить рецепт по ID",
            description = "Возвращает рецепт с указанным ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Рецепт найден"),
        @ApiResponse(responseCode = "404", description = "Рецепт не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable Long id) {
        visitCounter.incrementVisit("/api/recipes/" + id);
        RecipeDto recipe = recipeService.getRecipeById(id);
        return recipe != null ? ResponseEntity.ok(recipe) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Создать новый рецепт",
            description = "Создает новый рецепт с заданными параметрами.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Рецепт успешно создан"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    })
    @PostMapping
    public ResponseEntity<RecipeDto> createRecipe(@RequestBody RecipeDto recipeDto) {
        visitCounter.incrementVisit("/api/recipes");
        if (recipeDto == null
                || recipeDto.getTitle() == null
                || recipeDto.getDescription() == null) {
            throw new CustomException("Название и описание рецепта не могут быть пустыми");
        }
        RecipeDto createdRecipe = recipeService.createRecipe(recipeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipe);
    }

    @Operation(summary = "Обновить рецепт по ID",
            description = "Обновляет существующий рецепт с указанным ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Рецепт успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Рецепт не найден"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RecipeDto> updateRecipe(@PathVariable Long id,
                                                  @RequestBody RecipeDto recipeDto) {
        visitCounter.incrementVisit("/api/recipes/" + id);
        if (recipeDto == null
                || recipeDto.getTitle() == null
                || recipeDto.getDescription() == null) {
            throw new CustomException("Название и описание рецепта не могут быть пустыми");
        }
        RecipeDto updatedRecipe = recipeService.updateRecipe(id, recipeDto);
        return updatedRecipe != null
                ? ResponseEntity.ok(updatedRecipe) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Удалить рецепт по ID",
            description = "Удаляет рецепт с указанным ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Рецепт успешно удален"),
        @ApiResponse(responseCode = "404", description = "Рецепт не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить рецепты по названию кухни",
            description = "Возвращает список рецептов для указанной кухни.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Успешно получены рецепты для указанной кухни"),
        @ApiResponse(responseCode = "404",
                    description = "Кухня не найдена")
    })
    @GetMapping("/cuisine/{cuisineName}")
    public List<RecipeDto> getRecipesByCuisineName(@PathVariable String cuisineName) {
        visitCounter.incrementVisit("/api/recipes/cuisine/" + cuisineName);
        return recipeService.getRecipesByCuisineName(cuisineName);
    }

    @Operation(summary = "Создать несколько рецептов",
            description = "Создает несколько рецептов с заданными параметрами.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Рецепты успешно созданы"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    })
    @PostMapping("/bulk")
    public ResponseEntity<List<RecipeDto>> createRecipes(@RequestBody List<RecipeDto> recipeDtos) {
        visitCounter.incrementVisit("/api/recipes/bulk");
        if (recipeDtos == null || recipeDtos.isEmpty()) {
            throw new CustomException("Список рецептов не может быть пустым");
        }

        List<RecipeDto> createdRecipes = recipeDtos.stream()
                .map(recipeDto -> {
                    if (recipeDto.getTitle() == null || recipeDto.getDescription() == null) {
                        throw new CustomException("Название и описание не могут быть пустыми");
                    }
                    return recipeService.createRecipe(recipeDto);
                })
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecipes);
    }
}