import React, {useState, useEffect, useReducer} from 'react';
import { Card, Rate, Button, Modal, Input, Divider, Select, Collapse } from 'antd';
import { createReview, deleteRecipe, updateRecipe, updateReview, deleteReview, fetchIngredients, fetchCuisines, fetchReviews } from '../api/api';
import { EditOutlined, DeleteOutlined, PlusCircleOutlined, SearchOutlined } from '@ant-design/icons';
import { motion, AnimatePresence } from 'framer-motion';

const { Option } = Select;
const { Search } = Input;

const RecipeList = ({ recipes, refreshRecipes }) => {

    const [visibleAddReview, setVisibleAddReview] = useState(false);
    const [visibleUpdateRecipe, setVisibleUpdateRecipe] = useState(false);
    const [visibleDeleteConfirm, setVisibleDeleteConfirm] = useState(false);
    const [selectedRecipe, setSelectedRecipe] = useState(null);

    const [newReviewText, setNewReviewText] = useState('');
    const [newReviewRating, setNewReviewRating] = useState(0);
    const [currentRecipeId, setCurrentRecipeId] = useState(null);

    const [editingReview, setEditingReview] = useState(null);
    const [visibleEditReview, setVisibleEditReview] = useState(false);
    const [visibleDeleteReviewConfirm, setVisibleDeleteReviewConfirm] = useState(false);
    const [reviewToDelete, setReviewToDelete] = useState(null)

    const [updateRecipeTitle, setUpdateRecipeTitle] = useState('');
    const [updateRecipeDescription, setUpdateRecipeDescription] = useState('');
    const [updateRecipeIngredients, setUpdateRecipeIngredients] = useState([]);
    const [updateCuisineId, setUpdateCuisineId] = useState(null);

    const [ingredientOptions, setIngredientOptions] = useState([]);
    const [cuisineOptions, setCuisineOptions] = useState([]);
    const [recipeToDelete, setRecipeToDelete] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [editingRecipe, setEditingRecipe] = useState(null);
    const [, forceUpdate] = useReducer(
        (state) => state + 1,
        0,
        () => 0
    );


    const loadOptions = async () => {
        try {
            const [ingredients, cuisines] = await Promise.all([
                fetchIngredients(),
                fetchCuisines(),
            ]);
            setIngredientOptions(ingredients);
            setCuisineOptions(cuisines);
        } catch (error) {
            console.error('Ошибка при загрузке опций:', error);
        }
    };

    useEffect(() => {
        loadOptions();
    }, []);


    const theme = {
        primaryColor: '#58a36c',
        secondaryColor: '#ffffff',
        accentColor: '#3d7a4d',
        textColor: '#333333',
        lightBg: '#f8f8f8',
        cardShadow: '0 4px 12px rgba(0, 0, 0, 0.08)'
    };

    const cardAnimation = {
        initial: { opacity: 0, y: 20 },
        animate: { opacity: 1, y: 0 },
        exit: { opacity: 0, scale: 0.95 },
        transition: { duration: 0.3 }
    }

    const searchAnimation = {
        initial: { opacity: 0, y: -20 },
        animate: { opacity: 1, y: 0 },
        transition: { duration: 0.4, delay: 0.1 }
    };

    const filteredRecipes = recipes.filter(recipe => {
        const term = searchTerm.toLowerCase();
        return (
            recipe.title.toLowerCase().includes(term) ||
            recipe.description.toLowerCase().includes(term) ||
            recipe.ingredients.some(i => i.name.toLowerCase().includes(term)) ||
            recipe.cuisine.name.toLowerCase().includes(term)
        );
    });

    const calculateAverageRating = (reviews) => {
        if (!reviews || !reviews.length) return 0;
        const total = reviews.reduce((acc, review) => acc + (review.rating || 0), 0);
        return Number((total / reviews.length).toFixed(1));
    };

    const handleAddReview = (recipeId, e) => {
        e.stopPropagation();
        setCurrentRecipeId(recipeId);
        setVisibleAddReview(true);
    };

    const handleCancelAddReview = () => {
        setVisibleAddReview(false);
        setNewReviewText('');
        setNewReviewRating(0);
    };

    const refreshReviews = async (recipeId) => {
        try {
            const reviews = await fetchReviews(recipeId);

            if (selectedRecipe && selectedRecipe.id === recipeId) {
                setSelectedRecipe(prev => ({
                    ...prev,
                    reviews: reviews,

                }));
            }

            const updatedRecipes = recipes.map(recipe => {
                if (recipe.id === recipeId) {
                    return {
                        ...recipe,
                        reviews: reviews,
                    };
                }
                return recipe;
            });
            await refreshRecipes(updatedRecipes);
            return  calculateAverageRating(reviews);

        } catch (error) {
            console.error('Ошибка при обновлении отзывов:', error);
            return null;
        }
    };


    const submitReview = async () => {
        if (!newReviewText || newReviewRating === 0) {
            console.log('Отзыв не отправлен: отсутствует текст или рейтинг');
            return;
        }

        console.log('Создание отзыва для рецепта ID:', currentRecipeId);
        console.log('Текст отзыва:', newReviewText);
        console.log('Рейтинг:', newReviewRating);

        const reviewData = {
            text: newReviewText,
            rating: newReviewRating,
            recipe: { id: currentRecipeId }
        };

        console.log('Отправляемые данные:', JSON.stringify(reviewData, null, 2));

        try {
            console.log('Отправка запроса на создание отзыва...');
            const response = await createReview(reviewData);
            console.log('Отзыв успешно создан! Ответ сервера:', response);

            handleCancelAddReview();
            await refreshReviews(currentRecipeId);
            console.log('Модальное окно закрыто');

            await refreshRecipes();
            console.log('Список рецептов обновлен');
        } catch (error) {
            console.error('Ошибка при создании отзыва:', error);
            console.log('ID рецепта, на котором произошла ошибка:', currentRecipeId);
            console.log('Данные, которые не удалось отправить:', reviewData);
        }
    };

    const showFullRecipe = (recipe) => {
        if (!visibleUpdateRecipe && !visibleAddReview && !visibleDeleteConfirm) {
            setSelectedRecipe(recipe);
        }
    };

    const handleDeleteRecipe = (recipeId, e) => {
        e.stopPropagation();
        setRecipeToDelete(recipeId);
        setVisibleDeleteConfirm(true);
    };

    const confirmDeleteRecipe = async () => {
        try {
            await deleteRecipe(Number(recipeToDelete));
            await refreshRecipes();
            setVisibleDeleteConfirm(false);
            setRecipeToDelete(null);
        } catch (error) {
            console.error('Ошибка при удалении рецепта:', error);
        }
    };

    const handleOpenUpdateRecipe = (recipe, e) => {
        e.stopPropagation();
        e.preventDefault();
        setEditingRecipe(recipe);
        setUpdateRecipeTitle(recipe.title);
        setUpdateRecipeDescription(recipe.description);
        setUpdateRecipeIngredients(recipe.ingredients.map(i => i.id));
        setUpdateCuisineId(recipe.cuisine.id);
        setVisibleUpdateRecipe(true);
    };

    const submitUpdateRecipe = async () => {
        if (!editingRecipe) return;

        const updatedRecipeData = {
            title: updateRecipeTitle,
            description: updateRecipeDescription,
            ingredients: updateRecipeIngredients.map(id => ({ id })),
            cuisine: { id: updateCuisineId },
        };

        try {
            await updateRecipe(editingRecipe.id, updatedRecipeData);
            await refreshRecipes();
            setVisibleUpdateRecipe(false);
            setEditingRecipe(null);
        } catch (error) {
            console.error('Ошибка при обновлении рецепта:', error);
        }
    };
    const handleEditReview = (review, e) => {
        e.stopPropagation();
        setEditingReview({
            ...review,
            recipe: { id: selectedRecipe.id }
        });
        setNewReviewText(review.text);
        setNewReviewRating(review.rating);
        setVisibleEditReview(true);
    };

    const handleDeleteReview = (reviewId, e) => {
        e.stopPropagation();
        setReviewToDelete(reviewId);
        setVisibleDeleteReviewConfirm(true);
    };

    const submitEditReview = async () => {
        if (!editingReview) return;
        try {
            await updateReview(editingReview.id, {
                text: newReviewText,
                rating: newReviewRating
            });
            console.log('Отзыв успешно обновлен');
            await refreshReviews(editingReview.recipe.id);
            await refreshRecipes();
            forceUpdate();
            setVisibleEditReview(false);
            setEditingReview(null);
        } catch (error) {
            console.error('Ошибка при обновлении отзыва:', error);
        }
    };

    const confirmDeleteReview = async () => {
        if (!reviewToDelete || !selectedRecipe) return;

        try {
            await deleteReview(reviewToDelete);
            console.log('Отзыв успешно удален');
            await refreshReviews(selectedRecipe.id);
            await refreshRecipes();
            forceUpdate();
            setVisibleDeleteReviewConfirm(false);
            setReviewToDelete(null);
        } catch (error) {
            console.error('Ошибка при удалении отзыва:', error);
        }
    };

    const descriptionBlockStyle = {
        width: '100%',
        minHeight: '60px',
        backgroundColor: '#ffffff',
        borderRadius: '8px',
        padding: '8px',
        overflow: 'hidden',
        boxSizing: 'border-box',
        marginBottom: '8px',
        transform: 'translateX(-8px)',
    };

    const ingredientsBlockStyle = {
        width: '100%',
        minHeight: '60px',
        backgroundColor: '#ffffff',
        borderRadius: '8px',
        padding: '8px',
        overflow: 'hidden',
        boxSizing: 'border-box',
        marginBottom: '8px',
        transform: 'translateX(-8px)',
    };

    const cuisineBlockStyle = {
        width: '100%',
        minHeight: '40px',
        backgroundColor: '#ffffff',
        borderRadius: '8px',
        padding: '8px',
        overflow: 'hidden',
        boxSizing: 'border-box',
        marginBottom: '8px',
        transform: 'translateX(-8px)',
    };

    const descriptionTextStyle = {
        margin: 0,
        overflow: 'hidden',
        textOverflow: 'ellipsis',
        display: '-webkit-box',
        WebkitLineClamp: 2,
        WebkitBoxOrient: 'vertical',
        minHeight: '40px',
    };

    const ingredientsTextStyle = {
        margin: 0,
        overflow: 'hidden',
        textOverflow: 'ellipsis',
        display: '-webkit-box',
        WebkitLineClamp: 2,
        WebkitBoxOrient: 'vertical',
        minHeight: '40px'
    };

    const cuisineTextStyle = {
        margin: 0,
        overflow: 'hidden',
        textOverflow: 'ellipsis',
        display: '-webkit-box',
        WebkitLineClamp: 1,
        WebkitBoxOrient: 'vertical'
    };

    const containerStyle = {
        backgroundColor: theme.secondaryColor,
        borderRadius: '12px',
        padding: '24px',
        boxShadow: theme.cardShadow,
        marginBottom: '24px'
    };

    const searchContainerStyle = {
        ...containerStyle,
        marginBottom: '24px'
    };

    const recipesContainerStyle = {
        ...containerStyle,
        backgroundColor: '#ffffff',
    };

    const renderReviews = () => {
        if (!selectedRecipe) return null;

        return (
            <div>
                <h4>Отзывы:</h4>
                {selectedRecipe.reviews.map(review => (
                    <div key={review.id} style={{
                        marginBottom: '12px',
                        padding: '12px',
                        backgroundColor: '#ffffff',
                        boxShadow: theme.cardShadow,
                        borderRadius: '4px'
                    }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <Rate disabled value={review.rating}
                                  key={`rate-${review.id}-${review.rating}`} />
                            <div>
                                <Button
                                    size="small"
                                    icon={<EditOutlined />}
                                    onClick={(e) => handleEditReview(review, e)}
                                    style={{ marginRight: 8 }}
                                />
                                <Button
                                    size="small"
                                    icon={<DeleteOutlined />}
                                    danger
                                    onClick={(e) => handleDeleteReview(review.id, e)}
                                />
                            </div>
                        </div>
                        <p style={{ marginTop: 8 }}>{review.text}</p>
                    </div>
                ))}
            </div>
        );
    };


    return (
        <div style={{ marginTop: '30px', padding: '0 16px' }}>
            <motion.div
                style={searchContainerStyle}
                {...searchAnimation}
            >
                <Search
                    placeholder="Поиск рецептов..."
                    allowClear
                    enterButton={<SearchOutlined />}
                    size="large"
                    onChange={e => setSearchTerm(e.target.value)}
                    value={searchTerm}
                    style={{
                        width: '100%',
                        borderColor: theme.primaryColor
                    }}
                    className="custom-search-btn"
                />
            </motion.div>

            <div style={recipesContainerStyle}>
                <AnimatePresence>
                    <div style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'center', gap: '24px' }}>
                        {filteredRecipes.map(recipe => (
                            <motion.div
                                key={recipe.id}
                                layout
                                {...cardAnimation}
                            >
                                <Card
                                    title={recipe.title}
                                    style={{
                                        width: 300,
                                        height: 485,
                                        display: 'flex',
                                        flexDirection: 'column',
                                        backgroundColor: theme.secondaryColor,
                                        border: 'none',
                                        borderRadius: '12px',
                                        boxShadow: theme.cardShadow,
                                        padding: '16px',
                                        transition: 'transform 0.2s',
                                    }}
                                    onMouseEnter={e => (e.currentTarget.style.transform = 'scale(1.02)')}
                                    onMouseLeave={e => (e.currentTarget.style.transform = 'scale(1)')}
                                    hoverable
                                    onClick={() => showFullRecipe(recipe)}
                                >
                                    <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
                                        <div style={descriptionBlockStyle}>
                                            <strong>Описание:</strong>
                                            <p style={descriptionTextStyle}>
                                                {recipe.description}
                                            </p>
                                        </div>

                                        <div style={ingredientsBlockStyle}>
                                            <strong>Ингредиенты:</strong>
                                            <p style={ingredientsTextStyle}>
                                                {recipe.ingredients.map(i => i.name).join(', ')}
                                            </p>
                                        </div>

                                        <div style={cuisineBlockStyle}>
                                            <strong>Кухня:</strong>
                                            <p style={cuisineTextStyle}>
                                                {recipe.cuisine.name}
                                            </p>
                                        </div>

                                        <div style={{ marginTop: 10 }}>
                                            <p style={{ margin: '0 0 4px' }}><strong>Отзывы:</strong> {recipe.reviews.length}</p>
                                            <Rate disabled value={calculateAverageRating(recipe.reviews)} style={{ marginBottom: 12 }} />
                                        </div>
                                    </div>

                                    <div style={{ display: 'flex', justifyContent: 'flex-start', gap: '8px', marginTop: '12px' }}>
                                        <Button
                                            icon={<PlusCircleOutlined />}
                                            type="primary"
                                            onClick={(e) => handleAddReview(recipe.id, e)}
                                            style={{
                                                backgroundColor: '#58a36c',
                                                borderColor: '#58a36c',
                                                color: 'white',
                                            }}
                                        />
                                        <Button
                                            icon={<EditOutlined />}
                                            type="default"
                                            onClick={(e) => handleOpenUpdateRecipe(recipe, e)}
                                            style={{
                                                backgroundColor: '#ffffff',
                                                borderColor: '#58a36c',
                                                color: '#58a36c',
                                            }}
                                        />
                                        <Button
                                            icon={<DeleteOutlined />}
                                            type="danger"
                                            onClick={(e) => handleDeleteRecipe(recipe.id, e)}
                                            style={{
                                                backgroundColor: '#ffffff',
                                                borderColor: '#58a36c',
                                                color: 'green',
                                            }}
                                        />
                                    </div>
                                </Card>
                            </motion.div>
                        ))}
                    </div>
                </AnimatePresence>
            </div>

            <Modal
                title={selectedRecipe?.title || ''}
                open={!!selectedRecipe}
                onCancel={() => setSelectedRecipe(null)}
                footer={null}
                width={800}
            >
                {selectedRecipe && (
                    <div>
                        <p><strong>Описание:</strong> {selectedRecipe.description}</p>
                        <p><strong>Ингредиенты:</strong> {selectedRecipe.ingredients.map(i => i.name).join(', ')}</p>
                        <p><strong>Кухня:</strong> {selectedRecipe.cuisine.name}</p>
                        <Divider />
                        {renderReviews()}
                    </div>
                )}
            </Modal>

            <Modal
                title="Редактировать отзыв"
                open={visibleEditReview}
                onCancel={() => {
                    setVisibleEditReview(false);
                    setEditingReview(null);
                }}
                onOk={submitEditReview}
                okText="Сохранить"
                cancelText="Отмена"
                okButtonProps={{ style: { backgroundColor: '#58a36c', borderColor: '#58a36c', color: 'white' } }}
            >
                <Input.TextArea
                    rows={4}
                    value={newReviewText}
                    onChange={(e) => setNewReviewText(e.target.value)}
                    placeholder="Введите текст отзыва"
                    style={{ marginBottom: '12px' }}
                />
                <Rate
                    onChange={setNewReviewRating}
                    value={newReviewRating}
                />
            </Modal>

            <Modal
                title="Подтверждение удаления"
                open={visibleDeleteReviewConfirm}
                onCancel={() => setVisibleDeleteReviewConfirm(false)}
                onOk={confirmDeleteReview}
                okText="Удалить"
                cancelText="Отмена"
                okButtonProps={{ style: { backgroundColor: '#58a36c', borderColor: '#58a36c', color: 'white' } }}
            >
                <p>Вы уверены, что хотите удалить этот отзыв?</p>
            </Modal>


            <Modal
                title="Добавить отзыв"
                open={visibleAddReview}
                onCancel={handleCancelAddReview}
                onOk={submitReview}
                okText="Сохранить"
                cancelText="Отмена"
                okButtonProps={{ style: { backgroundColor: '#58a36c', borderColor: '#58a36c', color: 'white' } }}
            >
                <Input.TextArea
                    rows={4}
                    value={newReviewText}
                    onChange={(e) => setNewReviewText(e.target.value)}
                    placeholder="Введите текст отзыва"
                    style={{ marginBottom: '12px' }}
                />
                <Rate
                    onChange={setNewReviewRating}
                    value={newReviewRating}
                />
            </Modal>

            <Modal
                title="Обновить рецепт"
                open={visibleUpdateRecipe}
                onCancel={() => setVisibleUpdateRecipe(false)}
                onOk={submitUpdateRecipe}
                okText="Сохранить"
                cancelText="Отмена"
                okButtonProps={{ style: { backgroundColor: '#58a36c', borderColor: '#58a36c', color: 'white' } }}
            >
                <Input
                    placeholder="Название рецепта"
                    value={updateRecipeTitle}
                    onChange={e => setUpdateRecipeTitle(e.target.value)}
                    style={{ marginBottom: 10 }}
                />
                <Input.TextArea
                    placeholder="Описание рецепта"
                    value={updateRecipeDescription}
                    onChange={e => setUpdateRecipeDescription(e.target.value)}
                    style={{ marginBottom: 10 }}
                />
                <Select
                    mode="multiple"
                    style={{ width: '100%', marginBottom: 10 }}
                    placeholder="Выберите ингредиенты"
                    value={updateRecipeIngredients}
                    onChange={setUpdateRecipeIngredients}
                >
                    {ingredientOptions.map(ingredient => (
                        <Option key={ingredient.id} value={ingredient.id}>{ingredient.name}</Option>
                    ))}
                </Select>
                <Select
                    style={{ width: '100%', marginBottom: 10 }}
                    placeholder="Выберите кухню"
                    value={updateCuisineId}
                    onChange={setUpdateCuisineId}
                >
                    {cuisineOptions.map(cuisine => (
                        <Option key={cuisine.id} value={cuisine.id}>{cuisine.name}</Option>
                    ))}
                </Select>
            </Modal>

            <Modal
                title="Подтверждение удаления"
                open={visibleDeleteConfirm}
                onCancel={() => setVisibleDeleteConfirm(false)}
                onOk={confirmDeleteRecipe}
                okText="Удалить"
                cancelText="Отмена"
                okButtonProps={{ style: { backgroundColor: '#58a36c', borderColor: '#58a36c', color: 'white' } }}
            >
                <p>Вы уверены, что хотите удалить этот рецепт?</p>
            </Modal>
        </div>
    );
};

export default RecipeList;