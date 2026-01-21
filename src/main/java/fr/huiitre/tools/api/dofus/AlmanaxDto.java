package fr.huiitre.tools.api.dofus;

import java.time.LocalDate;

import fr.huiitre.tools.application.dofus.item.ItemView;

public class AlmanaxDto {
    
    private final Long id;
    private final String name;
    private final String description;
    private final LocalDate date;
    private final ItemView item;
    private final Long quantity;

    public AlmanaxDto(
        Long id,
        String name,
        String description,
        LocalDate date,
        ItemView item,
        Long quantity
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.item = item;
        this.quantity = quantity;
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

    public ItemView getItem() {
        return item;
    }

    public Long getQuantity() {
        return quantity;
    }
}
