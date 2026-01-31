package fr.huiitre.tools.modules.dofus.sync.application.item;

import java.util.List;

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
