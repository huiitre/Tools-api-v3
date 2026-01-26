package fr.huiitre.tools.modules.dofus.assets.application.ports;

import java.util.List;

import fr.huiitre.tools.modules.dofus.sync.application.usecase.recipe.RecipeSyncData;

public interface RecipeDataProvider {
    
    List<RecipeSyncData> fetchAll();
}
