package fr.huiitre.tools.application.dofus.ports.providers;

import java.util.List;

import fr.huiitre.tools.application.dofus.sync.itemtype.ItemTypeSyncData;

/**
 * Port applicatif.
 *
 * Fournit les item types sous forme normalisée,
 * indépendamment de la source (assets, API, autre).
 */
public interface ItemTypeDataProvider {

    /**
     * Récupère l’ensemble des item types à synchroniser.
     */
    List<ItemTypeSyncData> fetchAll();
}
