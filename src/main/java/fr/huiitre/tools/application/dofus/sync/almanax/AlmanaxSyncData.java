package fr.huiitre.tools.application.dofus.sync.almanax;

import java.util.List;

public class AlmanaxSyncData {
    
    private final Long assetId;
    private final String name;
    private final String description;
    private final List<String> dates;

    public AlmanaxSyncData(
        Long assetId,
        String name,
        String description,
        List<String> dates
    ) {
        this.assetId = assetId;
        this.name = name;
        this.description = description;
        this.dates = dates;
    }

    public Long getAssetId() {
        return assetId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getDates() {
        return dates;
    }
}
