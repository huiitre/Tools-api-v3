package fr.huiitre.tools.application.dofus.ports.repositories;

import java.util.List;

import fr.huiitre.tools.domain.dofus.Almanax;

public interface AlmanaxRepository {

    List<Almanax> findAll();

    Long save(Almanax almanax);

    void update(Almanax almanax);
}
