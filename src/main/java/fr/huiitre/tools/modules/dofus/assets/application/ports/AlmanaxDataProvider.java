package fr.huiitre.tools.modules.dofus.assets.application.ports;

import java.util.List;

import fr.huiitre.tools.modules.dofus.sync.application.usecase.almanax.AlmanaxSyncData;

public interface AlmanaxDataProvider {

    List<AlmanaxSyncData> fetchAll();
}
