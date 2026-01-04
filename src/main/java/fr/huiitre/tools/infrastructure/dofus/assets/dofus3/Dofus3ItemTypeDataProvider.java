package fr.huiitre.tools.infrastructure.dofus.assets.dofus3;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.huiitre.tools.application.dofus.ports.providers.Dofus3LanguageDataProvider;
import fr.huiitre.tools.application.dofus.ports.providers.ItemTypeDataProvider;
import fr.huiitre.tools.application.dofus.sync.itemtype.ItemTypeSyncData;

public class Dofus3ItemTypeDataProvider implements ItemTypeDataProvider {

    private final Dofus3LocalAssetsReader assetsReader;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Dofus3LanguageDataProvider languageDataProvider;

    public Dofus3ItemTypeDataProvider(
            Dofus3LocalAssetsReader assetsReader,
            Dofus3LanguageDataProvider languageDataProvider) {
        this.assetsReader = assetsReader;
        this.languageDataProvider = languageDataProvider;
    }

    @Override
    public List<ItemTypeSyncData> fetchAll() {
        try {
            String json = assetsReader.readFile("item_types.json");
            JsonNode root = objectMapper.readTree(json);

            JsonNode refIds = root
                    .path("references")
                    .path("RefIds");

            List<ItemTypeSyncData> result = new ArrayList<>();

            for (JsonNode ref : refIds) {
                JsonNode data = ref.path("data");

                long assetId = data.path("id").asLong();
                long nameId = data.path("nameId").asLong();
                long categoryId = data.path("categoryId").asLong();

                String name = languageDataProvider.getString(nameId);

                result.add(
                        new ItemTypeSyncData(
                                assetId,
                                name, // clé de traduction pour l’instant
                                categoryId));
            }

            return result;

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to load item types from Dofus3 local assets",
                    e);
        }
    }
}
