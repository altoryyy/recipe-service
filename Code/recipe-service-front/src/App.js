import './App.css';
import React, { useEffect, useState } from 'react';
import { Layout, Button, Modal, Input, Select, Spin } from 'antd';
import RecipeList from './components/RecipeList';
import { createRecipe, fetchIngredients, fetchCuisines, createIngredient, fetchRecipes } from './api/api';
import useDebounce from './hooks/useDebounce';

const { Header, Content } = Layout;
const { Option } = Select;

const App = () => {
    const [visibleAddRecipe, setVisibleAddRecipe] = useState(false);
    const [newRecipeTitle, setNewRecipeTitle] = useState('');
    const [newRecipeDescription, setNewRecipeDescription] = useState('');
    const [newRecipeIngredients, setNewRecipeIngredients] = useState([]);
    const [ingredientOptions, setIngredientOptions] = useState([]);
    const [cuisineOptions, setCuisineOptions] = useState([]);
    const [newCuisineId, setNewCuisineId] = useState(null);
    const [newIngredientName, setNewIngredientName] = useState('');
    const [recipes, setRecipes] = useState([]);
    const [loading, setLoading] = useState(false);

    const debouncedTitle = useDebounce(newRecipeTitle, 300);
    const debouncedDescription = useDebounce(newRecipeDescription, 300);
    const debouncedIngredientName = useDebounce(newIngredientName, 300);

    useEffect(() => {
        const loadInitialData = async () => {
            try {
                setLoading(true);
                const [ingredients, cuisines] = await Promise.all([
                    fetchIngredients(),
                    fetchCuisines()
                ]);
                setIngredientOptions(ingredients);
                setCuisineOptions(cuisines);
                await refreshRecipes();
            } catch (error) {
                console.error('Ошибка при инициализации:', error);
            } finally {
                setLoading(false);
            }
        };
        loadInitialData();
    }, []);

    const refreshIngredients = async () => {
        try {
            const ingredients = await fetchIngredients();
            setIngredientOptions(ingredients);
        } catch (error) {
            console.error('Ошибка при обновлении ингредиентов:', error);
        }
    };

    const refreshRecipes = async (updatedRecipes) => {
        if (updatedRecipes) {
            setRecipes(updatedRecipes); // Полная замена списка
        } else {
            const freshRecipes = await fetchRecipes(); // Получаем свежие данные
            setRecipes(freshRecipes);
        }
    };

    const handleAddRecipe = () => {
        setVisibleAddRecipe(true);
    };

    const handleCancelAddRecipe = () => {
        setVisibleAddRecipe(false);
        setNewRecipeTitle('');
        setNewRecipeDescription('');
        setNewRecipeIngredients([]);
        setNewIngredientName('');
        setNewCuisineId(null);
    };

    const submitRecipe = async () => {
        const recipeData = {
            title: debouncedTitle,
            description: debouncedDescription,
            ingredients: newRecipeIngredients.map(id => ({ id })),
            cuisine: { id: newCuisineId },
        };

        try {
            await createRecipe(recipeData);
            await refreshRecipes();
            handleCancelAddRecipe();
        } catch (error) {
            console.error('Ошибка при добавлении рецепта:', error);
        }
    };

    const handleAddIngredient = async () => {
        if (debouncedIngredientName) {
            try {
                const newIngredient = await createIngredient({ name: debouncedIngredientName });
                console.log('Добавлен новый ингредиент:', newIngredient);
                await refreshIngredients();
                setNewIngredientName('');
            } catch (error) {
                console.error('Ошибка при добавлении ингредиента:', error);
            }
        }
    };

    return (
        <Layout style={{ minHeight: '100vh', background: '#ffffff' }}>
            <Header style={{
                display: 'flex',
                alignItems: 'center',
                position: 'fixed',
                width: '100%',
                zIndex: 1,
                backgroundColor: '#58a36c',
                boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)',
                padding: '0 20px',
            }}>
                <div className="logo" style={{ fontSize: '24px', color: '#ffffff', fontWeight: 'bold' }}>
                    🍽️ Рецепты
                </div>
                <Button
                    className="add-recipe-button"
                    type="primary"
                    onClick={handleAddRecipe}
                    style={{
                        marginLeft: 'auto',
                        backgroundColor: '#ffffff',
                        borderColor: '#ffffff',
                        color: '#58a36c',
                        fontWeight: 'bold',
                        borderRadius: '8px',
                        transition: 'all 0.3s ease',
                        boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
                    }}

                    onMouseEnter={(e) => {
                        e.currentTarget.style.backgroundColor = '#58a36c';
                        e.currentTarget.style.color = '#ffffff';
                        e.currentTarget.style.borderColor = '#58a36c';
                    }}
                    onMouseLeave={(e) => {
                        e.currentTarget.style.backgroundColor = '#ffffff';
                        e.currentTarget.style.color = '#58a36c';
                        e.currentTarget.style.borderColor = '#ffffff';
                    }}
                >
                    Добавить рецепт
                </Button>
            </Header>
            <Content style={{ padding: '60px 50px 50px', marginTop: 64 }}>
                <div style={{
                    padding: 24,
                    minHeight: 280,
                        background: '#ffffff',
                    borderRadius: 12,
                    marginTop: '-80px'
                }}>
                    {loading ? <Spin size="large" /> : <RecipeList recipes={recipes} refreshRecipes={refreshRecipes} />}
                </div>
            </Content>

            <Modal
                title={
                    <div>
                        <div>Добавить рецепт</div>
                        <div style={{
                            fontSize: 12,
                            color: '#888',
                            fontWeight: 'normal',
                            marginTop: 4
                        }}>
                            * - обязательные поля
                        </div>
                    </div>
                }
                open={visibleAddRecipe}
                onCancel={handleCancelAddRecipe}
                onOk={submitRecipe}
                okText="Сохранить"
                cancelText="Отмена"
                bodyStyle={{ padding: '24px', backgroundColor: '#ffffff'}}
                okButtonProps={{
                    style: {
                        backgroundColor: '#58a36c',
                        borderColor: '#58a36c',
                        color: 'white',
                    },
                }}
                cancelButtonProps={{
                    style: {
                        color: '#888',
                    },
                }}
            >
                <div style={{ marginBottom: '16px' }}>
                    <Input
                        style={{ marginBottom: 20 }}
                        placeholder="Название рецепта *"
                        value={newRecipeTitle}
                        onChange={e => setNewRecipeTitle(e.target.value)}
                    />
                    <Input.TextArea
                        style={{ marginBottom: 20 }}
                        placeholder="Описание рецепта *"
                        value={newRecipeDescription}
                        onChange={e => setNewRecipeDescription(e.target.value)}
                    />
                    <Select
                        mode="multiple"
                        style={{ width: '100%', marginBottom: 20 }}
                        placeholder="Выберите ингредиенты *"
                        value={newRecipeIngredients}
                        onChange={setNewRecipeIngredients}
                    >
                        {ingredientOptions.map(ingredient => (
                            <Option key={ingredient.id} value={ingredient.id}>{ingredient.name}</Option>
                        ))}
                    </Select>
                    <Select
                        style={{ width: '100%', marginBottom: 20 }}
                        placeholder="Выберите кухню *"
                        value={newCuisineId}
                        onChange={setNewCuisineId}
                    >
                        {cuisineOptions.map(cuisine => (
                            <Option key={cuisine.id} value={cuisine.id}>{cuisine.name}</Option>
                        ))}
                    </Select>
                    <Input
                        style={{ marginBottom: 20 }}
                        placeholder="Добавить новый ингредиент"
                        value={newIngredientName}
                        onChange={e => setNewIngredientName(e.target.value)}
                        onPressEnter={handleAddIngredient}
                    />
                    <Button
                        type="primary"
                        onClick={handleAddIngredient}
                        block
                        style={{
                            backgroundColor: '#58a36c',
                            borderColor: '#58a36c',
                            color: 'white',
                            fontWeight: 'bold',
                            borderRadius: '8px',
                        }}
                    >
                        Добавить ингредиент
                    </Button>
                </div>
            </Modal>
        </Layout>
    );
};

export default App;