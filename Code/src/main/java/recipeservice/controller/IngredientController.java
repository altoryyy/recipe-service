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
import recipeservice.dto.IngredientDto;
import recipeservice.exception.CustomException;
import recipeservice.service.IngredientService;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    @Operation(summary = "Получить все ингредиенты",
            description = "Возвращает список всех ингредиентов в системе.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно получены все ингредиенты"),
    })
    public List<IngredientDto> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить ингредиент по ID",
            description = "Возвращает ингредиент с указанным ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ингредиент найден"),
        @ApiResponse(responseCode = "404", description = "Ингредиент не найден")
    })
    public ResponseEntity<IngredientDto> getIngredientById(@PathVariable Long id) {
        IngredientDto ingredient = ingredientService.getIngredientById(id);
        return ingredient != null
                ? ResponseEntity.ok(ingredient)
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Создать новый ингредиент",
            description = "Создает новый ингредиент с заданными параметрами.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ингредиент успешно создан"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    })
    public ResponseEntity<IngredientDto> createIngredient(
            @RequestBody IngredientDto ingredientDto) {
        validateIngredientDto(ingredientDto);
        IngredientDto createdIngredient = ingredientService.createIngredient(ingredientDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIngredient);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить ингредиент по ID",
            description = "Обновляет существующий ингредиент с указанным ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ингредиент успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Ингредиент не найден"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    })
    public ResponseEntity<IngredientDto> updateIngredient(
            @PathVariable Long id, @RequestBody IngredientDto ingredientDto) {
        validateIngredientDto(ingredientDto);
        IngredientDto updatedIngredient = ingredientService.updateIngredient(id, ingredientDto);
        return updatedIngredient != null
                ? ResponseEntity.ok(updatedIngredient)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить ингредиент по ID",
            description = "Удаляет ингредиент с указанным ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Ингредиент успешно удален"),
        @ApiResponse(responseCode = "404", description = "Ингредиент не найден")
    })
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }

    private void validateIngredientDto(IngredientDto ingredientDto) {
        if (ingredientDto == null) {
            throw new CustomException("Ингредиент не может быть null");
        }
        if (ingredientDto.getName() == null || ingredientDto.getName().isEmpty()) {
            throw new CustomException("Название ингредиента не может быть пустым");
        }
    }
}
