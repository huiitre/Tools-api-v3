package fr.huiitre.tools.modules.dofus.assets.application.ports;

import java.util.List;

import fr.huiitre.tools.modules.dofus.sync.application.usecase.item.ItemSyncData;

/**
 * Port applicatif.
 *
 * Fournit les items sous forme normalisée,
 * indépendamment de la source (assets, API, autre).
 */
public interface ItemDataProvider {

    /**
     * Récupère l’ensemble des items à synchroniser.
     */
    List<ItemSyncData> fetchAll();
}
