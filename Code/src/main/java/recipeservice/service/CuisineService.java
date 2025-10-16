package recipeservice.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import recipeservice.dao.CuisineDao;
import recipeservice.dto.CuisineDto;
import recipeservice.model.Cuisine;

@Service
public class CuisineService {

    private final CuisineDao cuisineDao;

    public CuisineService(CuisineDao cuisineDao) {
        this.cuisineDao = cuisineDao;
    }

    public List<CuisineDto> getAllCuisines() {
        return cuisineDao.getAllCuisines().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CuisineDto getCuisineById(Long id) {
        Cuisine cuisine = cuisineDao.getCuisineById(id);
        return convertToDto(cuisine);
    }

    public CuisineDto createCuisine(CuisineDto cuisineDto) {
        Cuisine cuisine = convertToEntity(cuisineDto);
        Cuisine createdCuisine = cuisineDao.createCuisine(cuisine);
        return convertToDto(createdCuisine);
    }

    public CuisineDto updateCuisine(Long id, CuisineDto cuisineDto) {
        Cuisine cuisine = convertToEntity(cuisineDto);
        Cuisine updatedCuisine = cuisineDao.updateCuisine(id, cuisine);
        return convertToDto(updatedCuisine);
    }

    public void deleteCuisine(Long id) {
        cuisineDao.deleteCuisine(id);
    }

    private CuisineDto convertToDto(Cuisine cuisine) {
        return new CuisineDto(cuisine.getId(), cuisine.getName());
    }

    private Cuisine convertToEntity(CuisineDto cuisineDto) {
        return new Cuisine(cuisineDto.getId(), cuisineDto.getName());
    }
}