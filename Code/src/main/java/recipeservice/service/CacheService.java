package recipeservice.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import recipeservice.dto.RecipeDto;

@Service
public class CacheService {
    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);

    private final int maxCacheSize = 100;
    private final Map<String, CacheEntry> cache;

    private static class CacheEntry {
        List<RecipeDto> recipes;
        long expiryTime;

        CacheEntry(List<RecipeDto> recipes, long ttl) {
            this.recipes = recipes;
            this.expiryTime = System.currentTimeMillis() + ttl;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    public CacheService() {
        this.cache = new LinkedHashMap<String, CacheEntry>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                if (size() > maxCacheSize) {
                    logger.info("Cache is full, removing eldest entry: {}", eldest.getKey());
                    return true;
                }
                return false;
            }
        };
    }

    public List<RecipeDto> getCachedRecipes(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            logger.debug("Cache miss for key: {}", key);
            return null;
        }
        logger.info("Cache hit for key: {}", key);
        return entry.recipes;
    }

    public void cacheRecipes(String key, List<RecipeDto> recipes) {
        long ttl = 1000;

        if (cache.size() >= maxCacheSize) {
            String eldestKey = cache.keySet().iterator().next();
            cache.remove(eldestKey);
            logger.info("Evicting entry {} to make room", eldestKey);
        }

        cache.put(key, new CacheEntry(recipes, ttl));
        logger.info("Cache put for key: {}", key);
    }

    public void removeCachedRecipes(String key) {
        if (cache.containsKey(key)) {
            cache.remove(key);
            logger.info("Удалена запись кеша для ключа: {}", key);
        } else {
            logger.info("Запись кеша не найдена для ключа: {}", key);
        }
    }

    public void updateCache(String key, RecipeDto newRecipe) {
        List<RecipeDto> cachedRecipes = getCachedRecipes(key);

        if (cachedRecipes != null) {
            cachedRecipes.add(newRecipe); // Здесь возникает ошибка
            logger.info("Обновлен кеш для ключа: {}", key);
        } else {
            cacheRecipes(key, new ArrayList<>(List.of(newRecipe))); // Изменяемый список
            logger.info("Создан новый кеш для ключа: {}", key);
        }
    }
}