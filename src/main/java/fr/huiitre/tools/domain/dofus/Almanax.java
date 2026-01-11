package fr.huiitre.tools.domain.dofus;

import java.util.List;

public class Almanax {
    
    private final Long id;
    private final Long assetId;
    private String name;
    private String description;
    private List<String> dates;

    private Almanax(
        Long id,
        Long assetId,
        String name,
        String description,
        List<String> dates
    ) {
        this.id = id;
        this.assetId = assetId;
        this.name = name;
        this.description = description;
        this.dates = dates;
    }

    public static Almanax rehydrate(
        Long id,
        Long assetId,
        String name,
        String description,
        List<String> dates
    ) {
        if (id == null) {
            throw new IllegalArgumentException("ALMANAX_ID_REQUIRED");
        }

        return new Almanax(
            id,
            assetId,
            name,
            description,
            dates
        );
    }

    public static Almanax create(
        Long assetId,
        String name,
        String description,
        List<String> dates
    ) {
        return new Almanax(
            null,
            assetId,
            name,
            description,
            dates
        );
    }

    public void update(
        String name,
        String description,
        List<String> dates
    ) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }

        if (description != null && !description.isBlank()) {
            this.description = description;
        }

        if (dates != null && !dates.isEmpty()) {
            this.dates = dates;
        }

        validateFields();
    }
    
    private void validateFields() {
        if (assetId == null) {
            throw new IllegalArgumentException("ALMANAX_ASSET_ID_REQUIRED");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("ALMANAX_NAME_REQUIRED");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("ALMANAX_DESCRIPTION_REQUIRED");
        }
    }

    public Long getId() {
        return id;
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
