package fr.huiitre.tools.modules.riot.valorant.application.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import fr.huiitre.tools.modules.riot.valorant.application.view.ValorantSkinView;

public interface ValorantSkinRepository {

    List<ValorantSkinView> findAll();

    Optional<ValorantSkinView> findById(Long id);

    Optional<ValorantSkinView> findByLevelAssetId(UUID levelAssetId);
}
