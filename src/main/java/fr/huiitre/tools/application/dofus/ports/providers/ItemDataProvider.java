package fr.huiitre.tools.application.dofus.ports.providers;

import java.util.List;

import fr.huiitre.tools.application.dofus.sync.item.ItemSyncData;

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
