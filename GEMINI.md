Projet : Tools API (Mini-ERP Backend)

1. Mission & Stack

Rôle : Cœur logique du Mini-ERP.

Stack : Java 21, Spring Boot (ou Quarkus selon config).

Architecture : DDD Strict.

Domain : Invariants métier, Entités, Value Objects (0 dépendance externe).

Application : Orchestration des cas d'utilisation (Services applicatifs).

Infrastructure : Implémentations techniques (Persistence, Clients API).

API : Contrôleurs REST / DTOs.

Config : Configuration framework.

2. Protocole d'Initialisation (OBLIGATOIRE)

Utiliser @Google Drive pour lire le fichier INDEX_PROJETS dans le dossier Projets AI.

Scanner le contenu du sous-dossier /Tools.

Confirmer que le contexte est à jour avant toute réponse.

3. Règles de Développement

DDD : Bloquer systématiquement toute fuite de responsabilité entre les couches.

Charbon : Rigueur pédagogique absolue dans les explications techniques.

Pragmatisme : Code prêt à l'emploi pour environnement Linux/Docker.

4. Stack réelle (à jour)

Stack : Java 21, Spring Boot, JDBC (JdbcTemplate — pas d'ORM Hibernate).
Pas de Flyway/Liquibase — schéma BDD géré manuellement.
Chaque use case est un @Service implémentant SecuredUseCase (AOP intercepte execute()).
Sécurité : Spring Security (.anyRequest().authenticated()) + UseCaseAuthorizationAspect.
@RequiredRole sur les controllers est décoratif — la vraie sécurité est dans les use cases.

Hiérarchie des rôles (RoleHierarchy.java) :
  READ_ONLY (1) < USER (2) < MODERATOR (3) < TECH (4) < ADMIN (5) < OWNER (6)
  ADMIN est au-dessus de TECH. OWNER n'est utilisé dans aucun use case actuellement.

5. Discovery Log

[Architecture] Initialisation du squelette DDD Java 21.

[Feature] Workshop Links — COMPLÈTE.
- WorkshopLink (id, source, url, label, createdAt) dans l'agrégat Workshop.
- Enum LinkSource : DOFUSBOOK, CUSTOM. Extensible via @Service LinkSourceHandler.
- Validation URL par source, label auto-résolu à la création, libre à l'édition.
- Limite : 3 liens max par atelier.
- Table : tools_dofus.workshop_link.
- Routes : POST/PUT/DELETE /dofus/workshops/{id}/links[/{id}].

[Feature] Riot/Valorant refresh token — COMPLÈTE.
- Route : POST /riot/valorant/refresh-token → { accessToken, refreshToken }.
- client_id hardcodé : prod-xsso-playvalorant (pas de secret).
- Appel POST form-urlencoded vers auth.riotgames.com/token.
- ModuleCode.RIOT ajouté à l'enum ModuleCode.
- Config : RiotConfig, adapter : RiotAuthHttpAdapter.

[Feature] Riot/Valorant skins + my-skins + watchlist — COMPLÈTE (2026-05-08).
- GET  /riot/valorant/skins              → List<ValorantSkinView> (READ_ONLY).
- GET  /riot/valorant/my-skins           → List<ValorantUserSkinView> (READ_ONLY).
- POST /riot/valorant/my-skins           → 201 — body : { "skinId": Long } (USER).
- DELETE /riot/valorant/my-skins/{skinId} → 204 (USER).
- GET  /riot/valorant/watchlist          → List<ValorantWatchlistEntryView> (READ_ONLY).
- POST /riot/valorant/watchlist          → 201 — body : { "skinId": Long } (USER).
- DELETE /riot/valorant/watchlist/{skinId} → 204 (USER).
- Table BDD : tools_riot.valorant_weapon_skins (id, asset_id UUID, name, icon_url, tier_uuid UUID, content_tier_uuid UUID).
- Ports : ValorantSkinRepository, ValorantUserSkinRepository, ValorantWatchlistRepository. Config : RiotConfig.

[Feature] Riot/Valorant sync skins — COMPLÈTE (2026-05-08).
- Route : POST /riot/valorant/sync/skins → { created, updated, deleted } (TECH + RIOT).
- Architecture : modules/riot/valorant/sync/ — api / application / infrastructure.
- Port ValorantSkinDataProvider (interface) → adapter ValorantApiSkinDataProvider.
  URL : https://valorant-api.com/v1/weapons/skins?language=fr-FR.
  Mapping : uuid→assetId, displayName→name, displayIcon/levels[0].displayIcon→iconUrl,
            themeUuid→tierUuid, contentTierUuid→contentTierUuid.
- Port ValorantSkinSyncRepository → PostgresValorantSkinSyncRepository (même table).
- Logique : fetch API → upsert (créer/mettre à jour) + supprimer ce qui n'existe plus.
- Extensibilité : implémenter ValorantSkinDataProvider + repointer RiotSyncConfig.
- Config : RiotSyncConfig (séparé de RiotConfig).

[Feature] Admin — gestion utilisateurs, stats & module users — COMPLÈTE.
- GET  /users                    → List<UserAdminView> (id, email, name, active, createdAt, avatarUrl, roles[Long]).
- GET  /users/{userId}           → UserProfileDto complet (roles + modules).
- PUT  /users/{userId}/role      → body { "roleId": Long } — remplace le rôle global.
- GET  /admin/stats              → { totalUsers, activeUsers, newUsersThisWeek, usersPerModule[] }.
- GET  /modules/{moduleId}/users → List<ModuleUserView> (userId, email, name, roleId, roleCode).
- UserRepository.findAllForAdmin() : JOIN users + user_role + role + user_auth_provider en 1 requête.
- UserRoleRepository.deleteAllByUserId() : remplacement atomique du rôle global.
- UserModuleRoleRepository.findAllByModuleId() : inverse de findAllByUserId.
- Config : AdminConfig wire PostgresAdminStatsRepository.

[Sécurité] Inversion hiérarchie ADMIN/TECH — ADMIN (5) > TECH (4).
- Use cases modules (GET/POST/PUT/DELETE) abaissés de TECH à ADMIN.
- GetAllRolesUseCase abaissé de TECH à ADMIN.