package fr.huiitre.tools.application.dofus.ports.providers;

import java.util.List;

import fr.huiitre.tools.application.dofus.sync.almanax.AlmanaxSyncData;

public interface AlmanaxDataProvider {
    
    List<AlmanaxSyncData> fetchAll();
}
