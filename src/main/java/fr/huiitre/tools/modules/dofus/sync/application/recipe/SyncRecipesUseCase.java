package fr.huiitre.tools.modules.dofus.sync.application.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.modules.core.module.domain.ModuleCode;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.core.security.application.usecase.SecuredUseCase;
import fr.huiitre.tools.modules.dofus.game.application.view.GameVersionData;
import fr.huiitre.tools.modules.dofus.item.application.ports.ItemRepository;
import fr.huiitre.tools.modules.dofus.item.domain.Item;
import fr.huiitre.tools.modules.dofus.recipe.application.ports.RecipeRepository;
import fr.huiitre.tools.modules.dofus.sync.application.sync.usecase.SyncReport;

@Service
@Transactional
public class SyncRecipesUseCase implements SecuredUseCase {

    private static final Logger logger = LoggerFactory.getLogger(SyncRecipesUseCase.class);

    private final ItemRepository itemRepository;
    private final RecipeDataProvider recipeDataProvider;
    private final RecipeRepository recipeRepository;

    public SyncRecipesUseCase(
            ItemRepository itemRepository,
            RecipeDataProvider recipeDataProvider,
            RecipeRepository recipeRepository) {
        this.itemRepository = itemRepository;
        this.recipeDataProvider = recipeDataProvider;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.DOFUS);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.TECH;
    }

    public SyncReport execute(GameVersionData gameVersion) {

        // 1. Tous les items de la version
        List<Item> items = itemRepository.findAllByGameVersionId(gameVersion.getId());

        // 2. Index asset_id -> Item
        Map<Long, Item> itemsByAssetId = new HashMap<>();
        for (Item item : items) {
            itemsByAssetId.put(item.getAssetId(), item);
        }

        // 3. Données recettes externes
        List<RecipeSyncData> external = recipeDataProvider.fetchAll();

        List<String> created = new ArrayList<>();
        List<String> updated = new ArrayList<>();

        // 4. Sync
        for (RecipeSyncData recipe : external) {

            Item parent = itemsByAssetId.get(recipe.getItemId());
            Item ingredient = itemsByAssetId.get(recipe.getIngredientId());

            if (parent == null || ingredient == null) {
                logger.warn(
                        "Recipe ignored (missing item): parentAssetId={} ingredientAssetId={}",
                        recipe.getItemId(),
                        recipe.getIngredientId());
                continue;
            }

            Long itemId = parent.getId();
            Long ingredientId = ingredient.getId();
            Long quantity = recipe.getQuantity();

            if (!recipeRepository.exists(itemId, ingredientId)) {

                recipeRepository.insert(itemId, ingredientId, quantity);

                created.add(
                        "itemId=" + itemId
                                + " ingredientId=" + ingredientId
                                + " quantity=" + quantity);

                continue;
            }

            recipeRepository.update(itemId, ingredientId, quantity);

            updated.add(
                    "itemId=" + itemId
                            + " ingredientId=" + ingredientId
                            + " quantity=" + quantity);
        }

        return new SyncReport(
                "recipes",
                "Recipes",
                created,
                updated);
    }
}
