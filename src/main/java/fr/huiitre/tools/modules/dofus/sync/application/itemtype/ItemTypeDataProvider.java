package fr.huiitre.tools.modules.dofus.sync.application.itemtype;

import java.util.List;

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
