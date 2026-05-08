package fr.huiitre.tools.modules.riot.valorant.sync.application;

import fr.huiitre.tools.modules.riot.valorant.application.view.ValorantSkinView;

import java.util.List;

public interface ValorantSkinSyncRepository {
    List<ValorantSkinView> findAll();
    Long save(ValorantSkinSyncData data);
    void update(Long id, ValorantSkinSyncData data);
    void delete(Long id);
}
