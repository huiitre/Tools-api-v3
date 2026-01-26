package fr.huiitre.tools.modules.dofus.recipe.application.ports;

public interface RecipeRepository {
    
    void insert(Long itemId, Long ingredientId, Long quantity);

    void update(Long itemId, Long ingredientId, Long quantity);

    boolean exists(Long itemId, Long ingredientId);
}
