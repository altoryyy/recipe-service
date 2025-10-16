package recipeservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import recipeservice.log.LogJob;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    @PostMapping("/create")
    @Operation(summary = "Создать лог-файл", description = "Создает лог-файл асинхронно.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Задача по созданию лог-файла запущена"),
        @ApiResponse(responseCode = "400", description = "Некорректный формат даты")
    })
    public CompletableFuture<ResponseEntity<String>> createLogFile(@RequestParam String date) {
        return LogJob.createLogFileAsync(date)
                .thenApply(jobId -> ResponseEntity.status(202).body("Log ID: " + jobId))
                .exceptionally(ex -> ResponseEntity.badRequest().body("Error: " + ex.getMessage()));
    }

    @GetMapping("/status/{id}")
    @Operation(summary = "Получить статус создания лог-файла",
            description = "Возвращает статус по ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статус задачи возвращен"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<String> getLogStatus(@PathVariable int id) {
        String status = LogJob.getJobStatus(id);
        if ("Not Found".equals(status)) {
            return ResponseEntity.status(404).body("Not found");
        }
        return ResponseEntity.ok(status);
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "Скачать файл логов по ID", description = "Возвращает файл логов по ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
        @ApiResponse(responseCode = "404", description = "File not found"),
        @ApiResponse(responseCode = "400", description = "File in progress")
    })
    public ResponseEntity<byte[]> downloadLogFile(@PathVariable int id) {
        String status = LogJob.getJobStatus(id);
        if ("In Progress".equals(status)) {
            return ResponseEntity.badRequest().body("Файл еще создается.".getBytes());
        }

        try {
            String matchingFilePath = findLogFilePath(id);
            byte[] logFileContent = Files.readAllBytes(Paths.get(matchingFilePath));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=logfile_" + id + ".log")
                    .body(logFileContent);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String findLogFilePath(int id) throws IOException {
        try (Stream<Path> paths = Files.list(Paths.get("logs"))) {
            return paths.filter(path ->
                            path.getFileName().toString().startsWith("app.log." + id + "."))
                    .findFirst()
                    .orElseThrow(() -> new IOException("File not found"))
                    .toString();
        }
    }
}