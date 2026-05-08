package fr.huiitre.tools.modules.riot.valorant.sync.application;

import java.util.List;

public interface ValorantSkinDataProvider {
    List<ValorantSkinSyncData> fetchAll();
}
