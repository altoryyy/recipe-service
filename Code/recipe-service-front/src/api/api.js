import axios from 'axios';

const API_URL = 'http://localhost:8080/api';
console.log('[DEBUG] API URL:', API_URL);

export const fetchRecipes = async () => {
    const response = await axios.get(`${API_URL}/recipes`);
    return response.data;
};

export const fetchReviews = async (recipeId) => {
    try {
        const response = await axios.get(`${API_URL}/reviews/recipe/${recipeId}`);
        return response.data;
    } catch (error) {
        console.error('Ошибка при загрузке отзывов:', error);
        throw error;
    }
};


export const createReview = async (reviewData) => {
    const response = await axios.post(`${API_URL}/reviews`, reviewData);
    return response.data;
};

export const createRecipe = async (recipeData) => {
    const response = await axios.post(`${API_URL}/recipes`, recipeData);
    if (response.status !== 201) {
        throw new Error(`Ошибка: ${response.data.message}`);
    }
    return response.data;
};

export const fetchIngredients = async () => {
    const response = await axios.get(`${API_URL}/ingredients`);
    if (response.status !== 200) {
        throw new Error(`Ошибка: ${response.data.message}`);
    }
    return response.data;
};

export const fetchCuisines = async () => {
    const response = await axios.get(`${API_URL}/cuisines`);
    if (response.status !== 200) {
        throw new Error(`Ошибка: ${response.data.message}`);
    }
    return response.data;
};

export const createIngredient = async (ingredientData) => {
    const response = await axios.post(`${API_URL}/ingredients`, ingredientData);
    if (response.status !== 201) {
        throw new Error(`Ошибка: ${response.data.message}`);
    }
    return response.data;
};

export const deleteRecipe = async (recipeId) => {
    try {
        const response = await axios.delete(`${API_URL}/recipes/${recipeId}`);
        console.log('Ответ от сервера:', response);
        return response.data;
    } catch (error) {
        console.error('Ошибка при запросе к серверу:', error.response ? error.response.data : error);
        throw error;
    }
};

export const updateRecipe = async (recipeId, updatedRecipeData) => {
    const response = await axios.put(`${API_URL}/recipes/${recipeId}`, updatedRecipeData);
    if (response.status !== 200) {
        throw new Error(`Ошибка при обновлении рецепта: ${response.data.message}`);
    }
    return response.data;
};

export const updateReview = async (reviewId, updatedReviewData) => {
    const response = await axios.put(`${API_URL}/reviews/${reviewId}`, updatedReviewData);
    if (response.status !== 200) {
        throw new Error(`Ошибка при обновлении отзыва: ${response.data.message}`);
    }
    return response.data;
};

export const deleteReview = async (reviewId) => {
    try {
        const response = await axios.delete(`${API_URL}/reviews/${reviewId}`);
        return response.data;
    } catch (error) {
        console.error('Ошибка при запросе к серверу:', error.response ? error.response.data : error);
        throw error;
    }
};