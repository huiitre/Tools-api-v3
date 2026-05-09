package fr.huiitre.tools.modules.riot.sync.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.huiitre.tools.modules.riot.sync.application.ValorantSkinDataProvider;
import fr.huiitre.tools.modules.riot.sync.application.ValorantSkinSyncData;

public class ValorantLocalSkinDataProvider implements ValorantSkinDataProvider {

    private final ValorantLocalAssetsReader assetsReader;
    private final String assetsBaseUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ValorantLocalSkinDataProvider(ValorantLocalAssetsReader assetsReader, String assetsBaseUrl) {
        this.assetsReader = assetsReader;
        this.assetsBaseUrl = assetsBaseUrl;
    }

    @Override
    public List<ValorantSkinSyncData> fetchAll() {
        try {
            String json = assetsReader.readFile("weapons.json");
            JsonNode root = objectMapper.readTree(json);
            JsonNode data = root.path("data");

            List<ValorantSkinSyncData> result = new ArrayList<>();

            for (JsonNode weapon : data) {
                for (JsonNode skin : weapon.path("skins")) {
                    UUID assetId = UUID.fromString(skin.get("uuid").asText());
                    String name = skin.path("displayName").asText(null);
                    String iconUrl = resolveIconUrl(assetId, skin);
                    UUID tierUuid = parseUuid(skin.path("themeUuid").asText(null));
                    UUID contentTierUuid = parseUuid(skin.path("contentTierUuid").asText(null));

                    result.add(new ValorantSkinSyncData(assetId, name, iconUrl, tierUuid, contentTierUuid));
                }
            }

            return result;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load Valorant skins from local assets", e);
        }
    }

    private String resolveIconUrl(UUID assetId, JsonNode skin) {
        String displayIcon = skin.path("displayIcon").asText(null);
        if (displayIcon != null && !displayIcon.isBlank()) {
            return assetsBaseUrl + "/tools_riot/valorant/img/weaponskins/" + assetId + "/displayicon.png";
        }

        JsonNode levels = skin.path("levels");
        if (levels.isArray() && levels.size() > 0) {
            String levelIcon = levels.get(0).path("displayIcon").asText(null);
            if (levelIcon != null && !levelIcon.isBlank()) {
                return assetsBaseUrl + "/tools_riot/valorant/img/weaponskins/" + assetId + "/displayicon.png";
            }
        }

        return null;
    }

    private UUID parseUuid(String value) {
        return (value != null && !value.isBlank()) ? UUID.fromString(value) : null;
    }
}
