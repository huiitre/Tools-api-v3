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
import fr.huiitre.tools.modules.dofus.workshop.application.dto.WorkshopItemDetailDto;
import fr.huiitre.tools.modules.dofus.workshop.application.exception.WorkshopNotFoundException;
import fr.huiitre.tools.modules.dofus.workshop.application.repository.WorkshopRepository;
import fr.huiitre.tools.modules.dofus.workshop.domain.WorkshopItem;
import fr.huiitre.tools.modules.dofus.workshop.domain.WorkshopItemIngredient;

@Service
@Transactional
public class GetWorkshopDetailUseCase implements SecuredUseCase {

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

    public GetWorkshopDetailUseCase(
            AuthenticatedUserProvider authenticatedUserProvider,
            WorkshopRepository workshopRepository,
            RecipeRepository recipeRepository,
            ItemRepository itemRepository,
            ItemEnrichmentService itemEnrichmentService) {
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.workshopRepository = workshopRepository;
        this.recipeRepository = recipeRepository;
        this.itemRepository = itemRepository;
        this.itemEnrichmentService = itemEnrichmentService;
    }

    public List<WorkshopItemDetailDto> execute(Long workshopId, Long gameVersionId) {

        Long userId = authenticatedUserProvider.getUserId();

        boolean workshopExists = workshopRepository.existsByIdAndUserId(userId, workshopId);
        if (!workshopExists) {
            throw new WorkshopNotFoundException();
        }

        List<WorkshopItem> items = workshopRepository.findAllItemsByUserIdAndWorkshopId(userId, workshopId);

        // Récupérer tous les ingrédients + Map par ID
        Map<Long, List<WorkshopItemIngredient>> ingredientsByWorkshopItemId = new HashMap<>();
        Map<Long, WorkshopItemIngredient> ingredientsById = new HashMap<>();
        Set<Long> allItemIds = new HashSet<>();

        for (WorkshopItem item : items) {
            allItemIds.add(item.getItemId());
            List<WorkshopItemIngredient> ingredients = workshopRepository
                    .findAllIngredientsByUserIdAndWorkshopItemId(userId, item.getId());
            ingredientsByWorkshopItemId.put(item.getId(), ingredients);

            for (WorkshopItemIngredient ing : ingredients) {
                allItemIds.add(ing.getItemId());
                ingredientsById.put(ing.getId(), ing);
            }
        }

        // Charger toutes les recettes
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
        Map<Long, List<FarmZoneDto>> farmZonesByItemId = itemEnrichmentService
                .loadFarmZones(new ArrayList<>(allItemIds));
        Map<Long, List<ItemImageDto>> imagesByItemId = itemEnrichmentService
                .loadItemImages(new ArrayList<>(allItemIds));

        List<WorkshopItemDetailDto> itemList = new ArrayList<>();

        for (WorkshopItem item : items) {
            ItemDto itemDto = itemsById.get(item.getItemId());
            List<ItemImageDto> imagesDto = imagesByItemId.getOrDefault(item.getItemId(), List.of());
            List<FarmZoneDto> farmZonesDto = farmZonesByItemId.getOrDefault(item.getItemId(), List.of());

            ItemDto enrichedItemDto = new ItemDto(
                    itemDto.getId(),
                    itemDto.getName(),
                    itemDto.getDescription(),
                    itemDto.isHasRecipe(),
                    itemDto.getAssetId(),
                    itemDto.getGameVersionId(),
                    itemDto.getLevel(),
                    itemDto.getType(),
                    imagesDto,
                    null,
                    item.getQuantity(),
                    farmZonesDto);

            List<WorkshopItemIngredient> ingredients = ingredientsByWorkshopItemId.get(item.getId());
            List<WorkshopIngredientDetailDto> ingredientList = new ArrayList<>();

            for (WorkshopItemIngredient ingredient : ingredients) {
                // Déterminer l'item parent pour récupérer quantity_required
                Long parentItemId;
                if (ingredient.getParentIngredientId() == null) {
                    parentItemId = item.getItemId();
                } else {
                    WorkshopItemIngredient parentIngredient = ingredientsById.get(ingredient.getParentIngredientId());
                    parentItemId = parentIngredient.getItemId();
                }
                
                Long quantityRequired = recipesByItemId
                    .getOrDefault(parentItemId, Map.of())
                    .getOrDefault(ingredient.getItemId(), 0L);

                ItemDto ingredientItemDto = itemsById.get(ingredient.getItemId());
                List<ItemImageDto> imagesIngredientDto = imagesByItemId.getOrDefault(ingredient.getItemId(), List.of());
                List<FarmZoneDto> farmZonesIngredientDto = farmZonesByItemId.getOrDefault(ingredient.getItemId(),
                        List.of());

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
                        farmZonesIngredientDto);

                ingredientList.add(
                        new WorkshopIngredientDetailDto(
                                ingredient.getId(),
                                item.getId(),
                                enrichedIngredientItemDto,
                                ingredient.getParentIngredientId(),
                                ingredient.getQuantityObtained(),
                                quantityRequired));
            }

            WorkshopItemDetailDto itemDetail = new WorkshopItemDetailDto(
                    item.getId(),
                    workshopId,
                    enrichedItemDto,
                    item.getQuantity(),
                    ingredientList);

            itemList.add(itemDetail);
        }

        return itemList;
    }
}