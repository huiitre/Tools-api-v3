package fr.huiitre.tools.api.dofus;

import java.time.LocalDate;

public class AlmanaxDto {
    
    private final Long id;
    private final String name;
    private final String description;
    private final LocalDate date;

    public AlmanaxDto(
        Long id,
        String name,
        String description,
        LocalDate date
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }
}
