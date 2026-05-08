package fr.huiitre.tools.modules.riot.valorant.application.ports;

import fr.huiitre.tools.modules.riot.valorant.application.view.ValorantSkinView;

import java.util.List;
import java.util.Optional;

public interface ValorantSkinRepository {

    List<ValorantSkinView> findAll();

    Optional<ValorantSkinView> findById(Long id);
}
