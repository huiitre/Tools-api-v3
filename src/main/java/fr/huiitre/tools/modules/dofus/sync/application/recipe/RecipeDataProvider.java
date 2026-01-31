package fr.huiitre.tools.modules.dofus.sync.application.recipe;

import java.util.List;

public interface RecipeDataProvider {

    List<RecipeSyncData> fetchAll();
}
