package fr.huiitre.tools.modules.riot.valorant.application.ports;

import java.util.List;
import java.util.Optional;

import fr.huiitre.tools.modules.riot.valorant.application.view.ValorantBundleView;

public interface ValorantBundleRepository {
    List<ValorantBundleView> findAll();
    Optional<ValorantBundleView> findById(Long id);
}
