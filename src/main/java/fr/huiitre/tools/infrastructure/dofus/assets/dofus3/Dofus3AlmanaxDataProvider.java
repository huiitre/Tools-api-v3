package fr.huiitre.tools.infrastructure.dofus.assets.dofus3;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import fr.huiitre.tools.application.dofus.ports.providers.AlmanaxDataProvider;
import fr.huiitre.tools.application.dofus.ports.providers.Dofus3LanguageDataProvider;
import fr.huiitre.tools.application.dofus.sync.almanax.AlmanaxSyncData;
import fr.huiitre.tools.infrastructure.logging.DebugLogger;

public class Dofus3AlmanaxDataProvider implements AlmanaxDataProvider {

    private final Dofus3LocalAssetsReader assetsReader;
    private final Dofus3LanguageDataProvider languageDataProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final DebugLogger logger = DebugLogger.of(Dofus3AlmanaxDataProvider.class);

    public Dofus3AlmanaxDataProvider(
            Dofus3LocalAssetsReader assetsReader,
            Dofus3LanguageDataProvider languageDataProvider) {
        this.assetsReader = assetsReader;
        this.languageDataProvider = languageDataProvider;
    }

    @Override
    public List<AlmanaxSyncData> fetchAll() {

        try {
            String json = assetsReader.readFile("almanax.json");
            JsonNode root = objectMapper.readTree(json);

            JsonNode refIds = root
                .path("references")
                .path("RefIds");

            List<AlmanaxSyncData> result = new ArrayList<>();

            for (JsonNode ref : refIds) {

                JsonNode type = ref.path("type");
                JsonNode classitem = type.path("class");

                if (!"AlmanaxCalendarData".equals(classitem.asText())) {
                    continue;
                }

                JsonNode data = ref.path("data");

                Long assetId = data.path("id").asLong();
                String name = languageDataProvider.getString(data.path("nameId").asLong());
                String description = languageDataProvider.getString(data.path("descId").asLong());

                JsonNode dateNodes = data.path("dates");
                JsonNode dateArray = dateNodes.path("Array");

                List<String> dates = new ArrayList<>();
                for (JsonNode dateNode : dateArray) {
                    String dateStr = dateNode.asText();
                    dates.add(dateStr);
                }

                result.add(new AlmanaxSyncData(
                    assetId,
                    name,
                    description,
                    dates
                ));
            }

            return result;
        } catch(Exception e) {
            throw new RuntimeException("Erreur lors de la lecture des données Almanax Dofus3", e);
        }
    }
}
