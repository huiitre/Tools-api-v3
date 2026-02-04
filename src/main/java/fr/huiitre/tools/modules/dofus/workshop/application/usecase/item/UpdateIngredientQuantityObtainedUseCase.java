package fr.huiitre.tools.modules.dofus.workshop.application.usecase.item;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.modules.core.module.domain.ModuleCode;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.core.security.application.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.modules.core.security.application.usecase.SecuredUseCase;
import fr.huiitre.tools.modules.dofus.recipe.application.ports.RecipeRepository;
import fr.huiitre.tools.modules.dofus.recipe.domain.Recipe;
import fr.huiitre.tools.modules.dofus.workshop.application.exception.WorkshopNotFoundException;
import fr.huiitre.tools.modules.dofus.workshop.application.repository.WorkshopRepository;
import fr.huiitre.tools.modules.dofus.workshop.domain.WorkshopItem;
import fr.huiitre.tools.modules.dofus.workshop.domain.WorkshopItemIngredient;

@Service
@Transactional
public class UpdateIngredientQuantityObtainedUseCase implements SecuredUseCase {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final WorkshopRepository workshopRepository;
    private final RecipeRepository recipeRepository;

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.DOFUS);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.USER;
    }

    public UpdateIngredientQuantityObtainedUseCase(
        AuthenticatedUserProvider authenticatedUserProvider,
        WorkshopRepository workshopRepository,
        RecipeRepository recipeRepository
    ) {
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.workshopRepository = workshopRepository;
        this.recipeRepository = recipeRepository;
    }

    public void execute(Long workshopId, Long ingredientId, Long quantityObtained) {
        Long userId = authenticatedUserProvider.getUserId();

        boolean exists = workshopRepository.existsByIdAndUserId(userId, workshopId);
        if (!exists) {
            throw new WorkshopNotFoundException();
        }

        WorkshopItemIngredient ingredient = workshopRepository.findIngredientByIdAndUserId(userId, ingredientId)
            .orElseThrow(() -> new IllegalArgumentException("Ingredient not found"));

        // Vérifier si l'ingrédient est en mode "crafté" (a des sous-ingrédients)
        List<WorkshopItemIngredient> subIngredients = workshopRepository.findIngredientsByParentIngredientId(userId, ingredientId);
        if (!subIngredients.isEmpty()) {
            throw new IllegalArgumentException("Cannot update quantity of crafted ingredient");
        }

        // Déterminer l'item parent pour récupérer la quantité requise
        Long parentItemId;
        if (ingredient.getParentIngredientId() == null) {
            List<WorkshopItem> items = workshopRepository.findAllItemsByUserIdAndWorkshopId(userId, workshopId);
            WorkshopItem mainItem = items.stream()
                .filter(i -> i.getId().equals(ingredient.getWorkshopItemId()))
                .findFirst()
                .orElseThrow();
            parentItemId = mainItem.getItemId();
        } else {
            WorkshopItemIngredient parentIngredient = workshopRepository
                .findIngredientByIdAndUserId(userId, ingredient.getParentIngredientId())
                .orElseThrow();
            parentItemId = parentIngredient.getItemId();
        }

        // Récupérer la quantité requise depuis la recette du parent
        List<Recipe> parentRecipes = recipeRepository.findByItemId(parentItemId);
        Long quantityRequired = parentRecipes.stream()
            .filter(r -> r.getIngredientId().equals(ingredient.getItemId()))
            .map(Recipe::getQuantity)
            .findFirst()
            .orElse(0L);

        // Limiter au maximum requis
        Long finalQuantity = Math.min(quantityObtained, quantityRequired);

        workshopRepository.updateIngredientQuantityObtained(userId, ingredientId, finalQuantity);
    }
}