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

6. Module Riot — Valorant

6a. Auth
  POST /riot/valorant/refresh-token → 200 { accessToken, refreshToken }
  - Reçoit un refreshToken en body, appelle auth.riotgames.com/token.
  - client_id hardcodé : prod-xsso-playvalorant (public client, pas de secret).
  - Retourne le nouveau accessToken + refreshToken (null si Riot n'en émet pas de nouveau).
  - Use case requiert ModuleCode.RIOT + RoleCode.READ_ONLY.
  Adapter : RiotAuthHttpAdapter — POST form-urlencoded, ParameterizedTypeReference<Map<String,Object>>.
  Config : RiotConfig (aucune propriété externe, URL et client_id hardcodés).

6b. Skins — COMPLÈTE (2026-05-08)
  Routes :
    GET    /riot/valorant/skins              → List<ValorantSkinView> (READ_ONLY)
    GET    /riot/valorant/my-skins           → List<ValorantUserSkinView> (READ_ONLY)
    POST   /riot/valorant/my-skins           → 201 ValorantUserSkinView — body : { "skinId": Long } (USER)
    DELETE /riot/valorant/my-skins/{skinId}  → 204 (USER)
    GET    /riot/valorant/watchlist          → List<ValorantWatchlistEntryView> (READ_ONLY)
    POST   /riot/valorant/watchlist          → 201 ValorantWatchlistEntryView — body : { "skinId": Long } (USER)
    DELETE /riot/valorant/watchlist/{skinId} → 204 (USER)

  Table BDD : tools_riot.valorant_weapon_skins (id, asset_id UUID, name, icon_url, tier_uuid UUID, content_tier_uuid UUID)
  Ports : ValorantSkinRepository, ValorantUserSkinRepository, ValorantWatchlistRepository
  Config : RiotConfig wire les 3 repos Postgres.

6c. Sync — COMPLÈTE (2026-05-08)
  Route :
    POST /riot/valorant/sync/skins → 200 ValorantSyncReport { created, updated, deleted } (TECH)

  Architecture :
    modules/riot/valorant/sync/
    ├── api/         ValorantSyncController
    ├── application/ ValorantSkinDataProvider (port — source externe)
    │                ValorantSkinSyncRepository (port — DB)
    │                ValorantSkinSyncData (DTO entrant depuis le provider)
    │                ValorantSyncReport (record résultat)
    │                SyncValorantSkinsUseCase (RIOT + TECH)
    └── infrastructure/ ValorantApiSkinDataProvider (appelle valorant-api.com)
                        PostgresValorantSkinSyncRepository

  Logique de synchro (SyncValorantSkinsUseCase) :
    - Fetch depuis API → compare avec DB (clé : asset_id UUID).
    - Crée les skins absents, met à jour les modifiés, supprime ceux disparus de l'API.
    - Retourne { created, updated, deleted }.

  Adapter HTTP (ValorantApiSkinDataProvider) :
    - URL : https://valorant-api.com/v1/weapons/skins?language=fr-FR
    - GET via RestTemplate + ParameterizedTypeReference<Map<String,Object>>.
    - Mapping : uuid→assetId, displayName→name, displayIcon (fallback levels[0].displayIcon)→iconUrl,
                themeUuid→tierUuid, contentTierUuid→contentTierUuid.

  Extensibilité : pour changer de source, implémenter ValorantSkinDataProvider et repointer RiotSyncConfig.
  Config : RiotSyncConfig (séparé de RiotConfig).

7. Module Admin — Gestion utilisateurs & stats

Nouvelles routes (toutes requièrent RoleCode.ADMIN minimum) :
  GET  /users                  → List<UserAdminView> (id, email, name, active, createdAt, avatarUrl, roles[id])
  GET  /users/{userId}         → UserProfileDto (id, email, name, userType, active, roles[], modules[])
  PUT  /users/{userId}/role    → 204 — body : { "roleId": Long }
  GET  /admin/stats            → AdminStatsView (totalUsers, activeUsers, newUsersThisWeek, usersPerModule[])

Architecture :
  - AdminUserController (/users) + AdminStatsController (/admin).
  - Use cases : ListUsersUseCase, GetUserDetailUseCase, SetUserGlobalRoleUseCase, GetAdminStatsUseCase.
  - AdminStatsRepository port + PostgresAdminStatsRepository (3 requêtes SQL distinctes).
  - Config : AdminConfig wire PostgresAdminStatsRepository.

Modifications des repositories existants :
  - UserRepository + PostgresUserRepository : findAllForAdmin() — JOIN users + user_role + role + user_auth_provider (avatarUrl Google) en une requête, pattern ResultSetExtractor LinkedHashMap.
  - UserRoleRepository + PostgresUserRoleRepository : deleteAllByUserId() — utilisé par SetUserGlobalRoleUseCase (delete + insert = remplacement atomique du rôle global).

SetUserGlobalRoleUseCase : valide que user ET role existent, puis replace le rôle global (deleteAll + save).
  - Prend roleId (Long) en body, pattern identique à ChangeUserModuleRoleRequest.

Notes sécurité :
  - @RequiredRole sur les controllers est décoratif (aucun interceptor ne le lit).
  - La sécurité réelle est assurée par UseCaseAuthorizationAspect (intercepte execute()).
  - Spring Security bloque les anonymes avant même d'atteindre les use cases (.anyRequest().authenticated()).

8. Sécurité — Hiérarchie des rôles (à jour)

Fichier : modules/core/security/infrastructure/RoleHierarchy.java
Ordre actuel (du plus bas au plus haut) :
  READ_ONLY (1) < USER (2) < MODERATOR (3) < TECH (4) < ADMIN (5) < OWNER (6)

ADMIN est au-dessus de TECH. OWNER n'est requis par aucun use case actuellement.
@RequiredRole sur les controllers est décoratif — seul UseCaseAuthorizationAspect enforce réellement.
Spring Security bloque les anonymes (.anyRequest().authenticated()) avant d'atteindre les use cases.

Routes accessibles à partir de ADMIN (minimum) :
  - Toute la gestion des modules (GET/POST/PUT/DELETE /modules, /modules/{id}/users, etc.)
  - Toute la gestion des users admin (/users, /admin/stats)
  - Synchro Dofus (TECH suffit, mais ADMIN passe aussi depuis l'inversion)

9. Module Admin — Routes complètes (à jour 2026-05-08)

  GET  /users                      → List<UserAdminView> (id, email, name, active, createdAt, avatarUrl, roles[Long])
  GET  /users/{userId}             → UserProfileDto (id, email, name, userType, active, roles[], modules[])
  PUT  /users/{userId}/role        → 204 — body : { "roleId": Long } — remplace le rôle global
  GET  /admin/stats                → AdminStatsView (totalUsers, activeUsers, newUsersThisWeek, usersPerModule[])
  GET  /modules/{moduleId}/users   → List<ModuleUserView> (userId, email, name, roleId, roleCode)

UserAdminView : classe simple, roles = List<Long> (IDs), avatarUrl via LEFT JOIN user_auth_provider GOOGLE.
ModuleUserView : classe simple, une ligne par user, RowMapper simple (pas de N+1, 1 role par user par module).
UserModuleRoleRepository.findAllByModuleId() : JOIN user_module_role + users + role WHERE module_id = ?

10. Discovery Log

[Architecture] Initialisation du squelette DDD Java 21.
[Feature] Workshop Links — backend complet (voir section 5).
[Feature] Riot/Valorant refresh token — backend complet (voir section 6a).
[Feature] Admin routes (users + stats + module users) — backend complet (voir sections 7 et 9).
[Sécurité] Hiérarchie rôles inversée ADMIN/TECH — ADMIN (5) > TECH (4) (voir section 8).
[Feature] Riot/Valorant skins + my-skins + watchlist — backend complet (voir section 6b).
[Feature] Riot/Valorant sync skins — backend complet (voir section 6c).
