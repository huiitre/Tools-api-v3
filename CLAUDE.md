Projet : Tools API (Mini-ERP Backend)

1. Mission & Stack

Rôle : Cœur logique du Mini-ERP.

Stack : Java 21, Spring Boot, JDBC (JdbcTemplate — pas d'ORM Hibernate).

Architecture : DDD Strict.

Domain : Invariants métier, Entités, Value Objects (0 dépendance externe).

Application : Orchestration des cas d'utilisation (Services applicatifs).

Infrastructure : Implémentations techniques (Persistence via PostgresXxxRepository).

API : Contrôleurs REST / DTOs.

2. Conventions absolues à respecter

- Pas de commentaires dans le code sauf si la raison est non évidente.
- Pas de Hibernate / JPA — uniquement JdbcTemplate avec des RowMapper statiques.
- Pas de Flyway/Liquibase — le schéma BDD est géré manuellement.
- Chaque use case est un @Service séparé implémentant SecuredUseCase.
- Les DTOs de réponse sont des records Java ou des classes simples (pas d'annotations Jackson).
- Les exceptions métier étendent IllegalArgumentException (→ 400 via ApiSecurityExceptionHandler).
- Ownership vérifiée côté use case (existsByIdAndUserId) ET dans le SQL (défense en profondeur).
- Utiliser RETURNING id pour récupérer l'ID généré après INSERT (pattern queryForObject).
- Les RowMapper sont des champs static final dans le repository.

3. Patterns observés

INSERT avec ID retourné :
  jdbcTemplate.queryForObject(sql_avec_RETURNING_id, Long.class, ...params)

Existence check ownership :
  workshopRepository.existsByIdAndUserId(userId, entityId)

Grouper des entités en une requête (éviter N+1) :
  jdbcTemplate.query(sql, rs -> { map.computeIfAbsent(...).add(...); }, params)

Gestion des erreurs JSON (enum invalide, etc.) :
  ApiSecurityExceptionHandler.handleNotReadable → message clair avec valeurs acceptées

4. Module Dofus — Workshop

Agrégat racine : Workshop (id, name, active, pinned, links)
Entités enfant : WorkshopItem, WorkshopItemIngredient, WorkshopTag, WorkshopLink

Table BDD cibles : tools_dofus.workshop, tools_dofus.workshop_item,
  tools_dofus.workshop_item_ingredient, tools_dofus.workshop_tag,
  tools_dofus.workshop_has_tag, tools_dofus.workshop_link

5. Feature Workshop Links — COMPLÈTE (2026-05-03)

Domaine : WorkshopLink (id, source, url, label, createdAt) dans l'agrégat Workshop.
Enum LinkSource : DOFUSBOOK, CUSTOM.

Architecture validation URL — extensible par source :
  interface LinkSourceHandler { source(), validateAndResolveLabel(url), default validate(url) }
  Implémenter @Service LinkSourceHandler → injecté automatiquement dans WorkshopLinkMetadataResolver.
  Handlers existants : DofusbookLinkSourceHandler, CustomLinkSourceHandler.

Formats Dofusbook acceptés :
  - https://d-bk.net/fr/d/{code}                              → "Dofus Book {code}"
  - https://www.dofusbook.net/fr/equipement/{id}-{slug}/objets → "Dofus Book {id|slug}"
  - https://www.dofusbook.net/fr/equipement/private/{id}-{slug}/objets → "Dofus Book {slug}"

Règles métier :
  - Max 3 liens par atelier (vérifié dans AddWorkshopLinkUseCase).
  - Label auto-résolu à la création, libre à l'édition (PUT envoie url + label).
  - URL validée à la création ET à l'édition selon la source du lien.
  - Table : tools_dofus.workshop_link (à créer manuellement).

Routes :
  POST   /dofus/workshops/{id}/links       → 201 WorkshopLinkDto
  PUT    /dofus/workshops/{id}/links/{id}  → 200 WorkshopLinkDto
  DELETE /dofus/workshops/{id}/links/{id}  → 204

WorkshopDto et WorkshopDetailResponse exposent la liste links.
État : Backend complet, prêt pour intégration Front.

6. Discovery Log

[Architecture] Initialisation du squelette DDD Java 21.
[Feature] Workshop Links — backend complet (voir section 5).
