package fr.huiitre.tools.application.dofus.assets.dofus3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AssetImageUrlBuilder {

    private final String baseUrl;

    public AssetImageUrlBuilder(
            @Value("${app.assets.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * @param category   dossier sous /img (item, monster, spell, etc.)
     * @param iconId     identifiant de l’asset
     * @param resolution X1 ou X2
     */
    public String build(
            String category,
            Long iconId,
            AssetResolution resolution) {
        String scale = resolution.getFolder();
        int size = resolution.getSize();

        return baseUrl
                + "/tools_dofus/dofus3/img/"
                + category + "/"
                + scale + "/"
                + iconId + "-" + size + ".png";
    }
}