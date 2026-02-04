package fr.huiitre.tools.modules.dofus.workshop.application.usecase.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.modules.core.module.domain.ModuleCode;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.core.security.application.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.modules.core.security.application.usecase.SecuredUseCase;
import fr.huiitre.tools.modules.dofus.item.application.dto.FarmZoneDto;
import fr.huiitre.tools.modules.dofus.item.application.dto.ItemDto;
import fr.huiitre.tools.modules.dofus.item.application.dto.ItemImageDto;
import fr.huiitre.tools.modules.dofus.item.application.ports.ItemRepository;
import fr.huiitre.tools.modules.dofus.item.application.service.ItemEnrichmentService;
import fr.huiitre.tools.modules.dofus.recipe.application.ports.RecipeRepository;
import fr.huiitre.tools.modules.dofus.recipe.domain.Recipe;
import fr.huiitre.tools.modules.dofus.workshop.application.dto.WorkshopIngredientDetailDto;
import fr.huiitre.tools.modules.dofus.workshop.application.exception.WorkshopNotFoundException;
import fr.huiitre.tools.modules.dofus.workshop.application.repository.WorkshopRepository;
import fr.huiitre.tools.modules.dofus.workshop.domain.WorkshopItem;
import fr.huiitre.tools.modules.dofus.workshop.domain.WorkshopItemIngredient;

@Service
@Transactional
public class CraftIngredientUseCase implements SecuredUseCase {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final WorkshopRepository workshopRepository;
    private final RecipeRepository recipeRepository;
    private final ItemRepository itemRepository;
    private final ItemEnrichmentService itemEnrichmentService;

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.DOFUS);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.USER;
    }

    public CraftIngredientUseCase(
        AuthenticatedUserProvider authenticatedUserProvider,
        WorkshopRepository workshopRepository,
        RecipeRepository recipeRepository,
        ItemRepository itemRepository,
        ItemEnrichmentService itemEnrichmentService
    ) {
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.workshopRepository = workshopRepository;
        this.recipeRepository = recipeRepository;
        this.itemRepository = itemRepository;
        this.itemEnrichmentService = itemEnrichmentService;
    }

    public List<WorkshopIngredientDetailDto> execute(Long workshopId, Long workshopItemId, Long ingredientId, Long gameVersionId) {
        Long userId = authenticatedUserProvider.getUserId();

        boolean exists = workshopRepository.existsByIdAndUserId(userId, workshopId);
        if (!exists) {
            throw new WorkshopNotFoundException();
        }

        Optional<WorkshopItemIngredient> parentIngredientOpt = workshopRepository.findIngredientByIdAndUserId(userId, ingredientId);
        if (parentIngredientOpt.isEmpty()) {
            throw new IllegalArgumentException("Ingredient not found");
        }

        WorkshopItemIngredient parentIngredient = parentIngredientOpt.get();
        Long itemIdToCraft = parentIngredient.getItemId();

        List<Recipe> recipes = recipeRepository.findByItemId(itemIdToCraft);
        List<WorkshopItemIngredient> ingredients = new ArrayList<>();

        for (Recipe recipe : recipes) {
            WorkshopItemIngredient ingredient = WorkshopItemIngredient.create(
                workshopItemId,
                recipe.getIngredientId(),
                ingredientId,
                0L
            );
            ingredients.add(ingredient);
        }

        workshopRepository.addIngredients(userId, ingredients);

        // Calculer quantityRequired du parentIngredient
        Long parentOfParentItemId;
        if (parentIngredient.getParentIngredientId() == null) {
            List<WorkshopItem> items = workshopRepository.findAllItemsByUserIdAndWorkshopId(userId, workshopId);
            WorkshopItem mainItem = items.stream()
                .filter(i -> i.getId().equals(workshopItemId))
                .findFirst()
                .orElseThrow();
            parentOfParentItemId = mainItem.getItemId();
        } else {
            WorkshopItemIngredient grandParent = workshopRepository
                .findIngredientByIdAndUserId(userId, parentIngredient.getParentIngredientId())
                .orElseThrow();
            parentOfParentItemId = grandParent.getItemId();
        }

        List<Recipe> recipesOfParent = recipeRepository.findByItemId(parentOfParentItemId);
        Long quantityRequired = recipesOfParent.stream()
            .filter(r -> r.getIngredientId().equals(itemIdToCraft))
            .map(Recipe::getQuantity)
            .findFirst()
            .orElse(0L);

        workshopRepository.updateIngredientQuantityObtained(userId, ingredientId, quantityRequired);

        return enrichCreatedIngredients(userId, ingredientId, gameVersionId);
    }

    private List<WorkshopIngredientDetailDto> enrichCreatedIngredients(
        Long userId,
        Long parentIngredientId,
        Long gameVersionId
    ) {
        List<WorkshopItemIngredient> ingredients = workshopRepository.findIngredientsByParentIngredientId(userId, parentIngredientId);

        Map<Long, WorkshopItemIngredient> ingredientsById = new HashMap<>();
        Set<Long> allItemIds = new HashSet<>();

        for (WorkshopItemIngredient ing : ingredients) {
            allItemIds.add(ing.getItemId());
            ingredientsById.put(ing.getId(), ing);
        }

        WorkshopItemIngredient parentIngredient = workshopRepository.findIngredientByIdAndUserId(userId, parentIngredientId).orElseThrow();
        allItemIds.add(parentIngredient.getItemId());

        Map<Long, Map<Long, Long>> recipesByItemId = new HashMap<>();
        for (Long itemId : allItemIds) {
            List<Recipe> recipes = recipeRepository.findByItemId(itemId);
            Map<Long, Long> ingredientQuantities = new HashMap<>();
            for (Recipe recipe : recipes) {
                ingredientQuantities.put(recipe.getIngredientId(), recipe.getQuantity());
            }
            recipesByItemId.put(itemId, ingredientQuantities);
        }

        Map<Long, ItemDto> itemsById = itemRepository.findByGameVersionIdAndItemIds(gameVersionId, allItemIds);
        Map<Long, List<FarmZoneDto>> farmZonesByItemId = itemEnrichmentService.loadFarmZones(new ArrayList<>(allItemIds));
        Map<Long, List<ItemImageDto>> imagesByItemId = itemEnrichmentService.loadItemImages(new ArrayList<>(allItemIds));

        List<WorkshopIngredientDetailDto> result = new ArrayList<>();

        for (WorkshopItemIngredient ingredient : ingredients) {
            Long quantityRequired = recipesByItemId
                .getOrDefault(parentIngredient.getItemId(), Map.of())
                .getOrDefault(ingredient.getItemId(), 0L);

            ItemDto ingredientItemDto = itemsById.get(ingredient.getItemId());
            List<ItemImageDto> imagesIngredientDto = imagesByItemId.getOrDefault(ingredient.getItemId(), List.of());
            List<FarmZoneDto> farmZonesIngredientDto = farmZonesByItemId.getOrDefault(ingredient.getItemId(), List.of());

            ItemDto enrichedIngredientItemDto = new ItemDto(
                ingredientItemDto.getId(),
                ingredientItemDto.getName(),
                ingredientItemDto.getDescription(),
                ingredientItemDto.isHasRecipe(),
                ingredientItemDto.getAssetId(),
                ingredientItemDto.getGameVersionId(),
                ingredientItemDto.getLevel(),
                ingredientItemDto.getType(),
                imagesIngredientDto,
                ingredientItemDto.getParentItemId(),
                quantityRequired,
                farmZonesIngredientDto
            );

            result.add(
                new WorkshopIngredientDetailDto(
                    ingredient.getId(),
                    ingredient.getWorkshopItemId(),
                    enrichedIngredientItemDto,
                    ingredient.getParentIngredientId(),
                    ingredient.getQuantityObtained(),
                    quantityRequired
                )
            );
        }

        return result;
    }
}