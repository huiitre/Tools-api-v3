package fr.huiitre.tools.modules.dofus.monster.application.dto;

public class MonsterDto {
    
    private final Long id;
    private final String name;

    public MonsterDto(
            Long id,
            String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
