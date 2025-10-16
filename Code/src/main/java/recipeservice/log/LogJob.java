package recipeservice.log;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogJob {
    private static final ConcurrentHashMap<Integer, String> jobStatus = new ConcurrentHashMap<>();

    static {
        initializeJobStatuses();
    }

    public static CompletableFuture<Integer> createLogFileAsync(String date) {
        int jobId = getNextAvailableId();
        jobStatus.put(jobId, "In Progress");

        CompletableFuture.runAsync(() -> {
            try {
                String sourceFilePath = "logs/app.log";
                String targetLogFilePath = "logs/app.log." + jobId + "." + date + ".log";

                createLogFileFromSource(sourceFilePath, targetLogFilePath, date);
                jobStatus.put(jobId, "Completed");
            } catch (Exception e) {
                jobStatus.put(jobId, "Failed: " + e.getMessage());
            }
        });

        return CompletableFuture.completedFuture(jobId);
    }

    private static void createLogFileFromSource(String sourceFilePath,
                                                String targetFilePath,
                                                String date) throws Exception {
        Thread.sleep(20000);

        try (Stream<String> lines = Files.lines(Paths.get(sourceFilePath))) {
            List<String> filteredLines = lines.filter(line -> line.contains(date))
                    .collect(Collectors.toList());

            Files.write(Paths.get(targetFilePath), filteredLines, StandardOpenOption.CREATE);
        }
    }

    private static int getNextAvailableId() {
        try (Stream<Path> paths = Files.list(Paths.get("logs"))) {
            return paths.map(path -> {
                String fileName = path.getFileName().toString();
                Pattern pattern = Pattern.compile("app\\.log\\.(\\d+)\\.");
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.find()) {
                    return Integer.parseInt(matcher.group(1));
                }
                return -1;
            })
                    .filter(id -> id != -1)
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0) + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    public static String getJobStatus(int jobId) {
        if (!jobStatus.containsKey(jobId)) {
            return checkFileStatus(jobId);
        }
        return jobStatus.get(jobId);
    }

    private static String checkFileStatus(int jobId) {
        String filePath = "logs/app.log." + jobId + ".";
        try (Stream<Path> paths = Files.list(Paths.get("logs"))) {
            return paths.filter(path -> path.getFileName().toString().startsWith(filePath))
                    .findFirst()
                    .map(path -> "File exists: " + path.getFileName().toString())
                    .orElse("Not Found");
        } catch (Exception e) {
            return "Not Found";
        }
    }

    private static void initializeJobStatuses() {
        try (Stream<Path> paths = Files.list(Paths.get("logs"))) {
            paths.forEach(path -> {
                String fileName = path.getFileName().toString();
                Pattern pattern = Pattern.compile("app\\.log\\.(\\d+)\\.");
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.find()) {
                    int id = Integer.parseInt(matcher.group(1));
                    jobStatus.put(id, "File exists: " + fileName);
                }
            });
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}