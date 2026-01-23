package fr.huiitre.tools.modules.dofus.assets.application.ports;

import java.util.List;

import fr.huiitre.tools.modules.dofus.sync.application.usecase.itemtype.ItemTypeSyncData;

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
