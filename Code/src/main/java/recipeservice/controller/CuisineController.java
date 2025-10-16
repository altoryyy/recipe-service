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
import recipeservice.dto.CuisineDto;
import recipeservice.exception.CustomException;
import recipeservice.log.VisitCounter;
import recipeservice.service.CuisineService;

@RestController
@RequestMapping("/api/cuisines")
public class CuisineController {

    private final CuisineService cuisineService;
    private final VisitCounter visitCounter;

    public CuisineController(CuisineService cuisineService, VisitCounter visitCounter) {
        this.cuisineService = cuisineService;
        this.visitCounter = visitCounter;
    }

    @Operation(summary = "Получить все кухни",
            description = "Возвращает список всех доступных кухонь.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно возвращен список кухонь"),
    })
    @GetMapping
    public List<CuisineDto> getAllCuisines() {
        visitCounter.incrementVisit("/api/cuisines");
        return cuisineService.getAllCuisines();
    }

    @Operation(summary = "Получить кухню по ID", description = "Возвращает кухню по указанному ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно возвращена кухня"),
        @ApiResponse(responseCode = "404", description = "Кухня не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CuisineDto> getCuisineById(@PathVariable Long id) {
        visitCounter.incrementVisit("/api/cuisines/" + id);
        CuisineDto cuisine = cuisineService.getCuisineById(id);
        return cuisine != null ? ResponseEntity.ok(cuisine) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Создать новую кухню",
            description = "Создает новую кухню с заданными параметрами.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Кухня успешно создана"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<CuisineDto> createCuisine(@RequestBody CuisineDto cuisineDto) {
        if (cuisineDto == null || cuisineDto.getName() == null) {
            throw new CustomException("Название кухни не может быть пустым");
        }
        CuisineDto createdCuisine = cuisineService.createCuisine(cuisineDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCuisine);
    }

    @Operation(summary = "Обновить кухню",
            description = "Обновляет существующую кухню по указанному ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Кухня успешно обновлена"),
        @ApiResponse(responseCode = "404", description = "Кухня не найдена"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CuisineDto> updateCuisine(@PathVariable Long id,
                                                    @RequestBody CuisineDto cuisineDto) {
        if (cuisineDto == null || cuisineDto.getName() == null) {
            throw new CustomException("Название кухни не может быть пустым");
        }
        CuisineDto updatedCuisine = cuisineService.updateCuisine(id, cuisineDto);
        return updatedCuisine != null
                ? ResponseEntity.ok(updatedCuisine)
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Удалить кухню",
            description = "Удаляет кухню по указанному ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Кухня успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Кухня не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCuisine(@PathVariable Long id) {
        cuisineService.deleteCuisine(id);
        return ResponseEntity.noContent().build();
    }
}